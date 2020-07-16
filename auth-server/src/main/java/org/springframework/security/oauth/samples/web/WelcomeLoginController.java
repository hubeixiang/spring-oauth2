package org.springframework.security.oauth.samples.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.samples.cache.RedisUtil;
import org.springframework.security.oauth.samples.web.url.AuthorizationCodeUrl;
import org.springframework.security.oauth.samples.web.url.CheckTokenUri;
import org.springframework.security.oauth.samples.web.url.ClientCredentialsUrl;
import org.springframework.security.oauth.samples.web.url.ImplicitUrl;
import org.springframework.security.oauth.samples.web.url.RSAUtils;
import org.springframework.security.oauth.samples.web.url.RefreshTokenUri;
import org.springframework.security.oauth.samples.web.url.ResourceOwnerPasswordCredentialsUrl;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Controller
public class WelcomeLoginController {
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
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model, HttpServletRequest request) {
        model.addAttribute("loginError", true);
        Object exception = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        Object info = null;
        if (exception instanceof SessionAuthenticationException) {
            info = "当前用户已在其他地方登录";
        } else {
            info = exception;
        }
        model.addAttribute("errorInfo", info);
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
}
