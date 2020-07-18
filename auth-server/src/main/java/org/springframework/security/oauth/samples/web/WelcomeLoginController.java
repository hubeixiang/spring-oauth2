package org.springframework.security.oauth.samples.web;

import org.framework.hsven.i18n.I18nMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.samples.cache.RedisUtil;
import org.springframework.security.oauth.samples.web.url.AuthorizationCodeUrl;
import org.springframework.security.oauth.samples.web.url.CheckTokenUri;
import org.springframework.security.oauth.samples.web.url.ClientCredentialsUrl;
import org.springframework.security.oauth.samples.web.url.ImplicitUrl;
import org.springframework.security.oauth.samples.web.url.RefreshTokenUri;
import org.springframework.security.oauth.samples.web.url.ResourceOwnerPasswordCredentialsUrl;
import org.springframework.security.oauth.samples.web.util.RSAUtils;
import org.springframework.security.oauth.samples.web.util.VerifyCodeInfo;
import org.springframework.security.oauth.samples.web.util.VerifyCodeUtil;
import org.springframework.security.oauth.samples.web.util.WebUtil;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class WelcomeLoginController {
    @Autowired
    private SessionRegistry sessionRegistry;

    private VerifyCodeUtil vc = new VerifyCodeUtil();

    @GetMapping("online")
    @ResponseBody
    public int onlineUsers() {
        List<Object> users = sessionRegistry.getAllPrincipals();
        return users.size();
    }

    @GetMapping("online/details")
    @ResponseBody
    public Object onlineUsersDetails() {
        return detailSessionRegistry();
    }

    @GetMapping("session/invalid")
    public String sessionInvalid() {
        return "redirect:/index";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/welcome")
    public String wellcome(Model model) {
        String date = DateUtils.format(new Date(), Locale.CHINA);
        String[] messages = new String[]{"auth server success", date};
        model.addAttribute("messages", messages);
        appendUserName(model);
        return "welcome";
    }

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        String date = DateUtils.format(new Date(), Locale.CHINA);
        String[] messages = new String[]{"index auth server success", date};
        model.addAttribute("messages", messages);
        appendUserName(model);
        appendOauthUrl(model, request);
        return "index";
    }

    @GetMapping("/vercode")
    public void code(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyCodeInfo verifyCodeInfo = vc.createVerifyCodeInfo();
        HttpSession session = req.getSession();
        session.setAttribute(VerifyCodeUtil.WEB_HTML_ID_KEY, verifyCodeInfo.getVerifyCode());
        VerifyCodeUtil.output(verifyCodeInfo.getBufferedImage(), resp.getOutputStream());
    }

    @GetMapping("/login")
    public String login(Model model) {
        appendUserName(model);
        try {
            //生成密钥对(公钥和私钥)
            Map<String, Object> msd = RSAUtils.genKeyPair();
            //获取私钥
            String privatekey = RSAUtils.getPrivateKey(msd);
            //获取公钥
            String publickey = RSAUtils.getPublicKey(msd);
            //把密钥对存入缓存
            //RedisUtil redis = new RedisUtil();
            //RedisUtil.set(publickey,privatekey);
            if (!RedisUtil.exists(RSAUtils.CACHE_KEY_PREFIX + publickey)) {
                RedisUtil.set(RSAUtils.CACHE_KEY_PREFIX + publickey, privatekey);
            }
            //request.getSession().setAttribute(publickey,privatekey);
            //传输公钥给前端用户
            model.addAttribute(RSAUtils.WEB_HTML_ID_KEY, publickey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        appendLoginTips(model);
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model, HttpServletRequest request) {
        model.addAttribute("loginError", true);
        Object exception = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        Object info = null;
        if (exception instanceof SessionAuthenticationException) {
            info = getMessage("WelcomeLoginController.user_alread_login");
        } else {
            info = exception;
        }
        if (info instanceof Exception) {
            model.addAttribute("errorInfo", ((Exception) info).getMessage());
        } else {
            model.addAttribute("errorInfo", info);
        }
        appendUserName(model);
        return login(model);
    }

    private void appendOauthUrl(Model model, HttpServletRequest request) {
        String httpPath = WebUtil.getBasePath(request);
        String redirect = String.format("%s/%s", httpPath, "index");
        String state = "xyzabcn";
        String contextPath = null;
        //authorizationCode
        AuthorizationCodeUrl authorizationCodeUrl = new AuthorizationCodeUrl(httpPath, contextPath, redirect, state);
        model.addAttribute("authorizationCode_code_url", authorizationCodeUrl.getCodeMyUrl());
        model.addAttribute("authorizationCode_code_token_url", authorizationCodeUrl.getTokenMyUrl());
        //client credentials
        ClientCredentialsUrl clientCredentialsUrl = new ClientCredentialsUrl(httpPath, contextPath, redirect);
        model.addAttribute("client_credentials_token_url", clientCredentialsUrl.getMyUrl());
        //implicit
        ImplicitUrl implicitUrl = new ImplicitUrl(httpPath, contextPath, redirect);
        model.addAttribute("implicit_token_url", implicitUrl.getMyUrl());
        model.addAttribute("spec_implicit_token_url", implicitUrl.getSpecUrl());
        //password
        String userName = getUserName();
        ResourceOwnerPasswordCredentialsUrl passwordCredentialsUrl = new ResourceOwnerPasswordCredentialsUrl(httpPath, contextPath, redirect, userName);
        model.addAttribute("password_token_url", passwordCredentialsUrl.getMyUrl());
        //refresh token
        RefreshTokenUri refreshTokenUri = new RefreshTokenUri(httpPath, contextPath);
        model.addAttribute("refresh_token_url", refreshTokenUri.getMyUrl());
        //check token
        CheckTokenUri checkTokenUri = new CheckTokenUri(httpPath, contextPath);
        model.addAttribute("check_token_url", checkTokenUri.getMyUrl());
    }

    private void appendUserName(Model model) {
        String userName = getUserName();
        if (!StringUtils.isEmpty(userName)) {
            model.addAttribute("loginUserName", userName);
        }
    }

    private String getUserName() {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (object instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) object;
            String username = userDetails.getUsername();
            return username;
        } else {
            return object.toString();
        }
    }


    private Object detailSessionRegistry() {
        //退出成功后删除当前用户session
        Map<String, List<String>> detail = new HashMap<>();
        List<Object> o = sessionRegistry.getAllPrincipals();
        for (Object principal : o) {
            if (principal instanceof User) {
                final User loggedUser = (User) principal;
                List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
                if (null != sessionsInfo && sessionsInfo.size() > 0) {
                    for (SessionInformation sessionInformation : sessionsInfo) {
                        if (!sessionInformation.isExpired()) {
                            //未过期的信息
                            if (detail.get(loggedUser.getUsername()) != null) {
                                detail.get(loggedUser.getUsername()).add(sessionInformation.getSessionId());
                            } else {
                                List<String> sessions = new ArrayList<>();
                                detail.put(loggedUser.getUsername(), sessions);
                                detail.get(loggedUser.getUsername()).add(sessionInformation.getSessionId());
                            }
                        }
                    }
                }
            }
        }
        return detail;
    }

    private void appendLoginTips(Model model) {
        model.addAttribute("page_title", getMessage("WelcomeLoginController.page_title"));
        model.addAttribute("header_title", getMessage("WelcomeLoginController.header_title"));
        model.addAttribute("box_Username", getMessage("WelcomeLoginController.box_Username"));
        model.addAttribute("box_Password", getMessage("WelcomeLoginController.box_Password"));
        model.addAttribute("box_verifyCode", getMessage("WelcomeLoginController.box_verifyCode"));
        model.addAttribute("submit_tip", getMessage("WelcomeLoginController.submit_tip"));
    }

    private String getMessage(String code) {
        return I18nMessageUtil.getInstance().getMessage(code, LocaleContextHolder.getLocale());
    }
}
