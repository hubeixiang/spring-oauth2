package org.springframework.security.oauth.samples.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.samples.web.url.AuthorizationCodeUrl;
import org.springframework.security.oauth.samples.web.url.ClientCredentialsUrl;
import org.springframework.security.oauth.samples.web.url.ImplicitUrl;
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

@Controller
public class WelcomeLoginController {
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
