package org.springframework.security.oauth.samples.custom;

import org.springframework.security.oauth.samples.web.url.RSAUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        String key = context.getParameter(RSAUtils.WEB_HTML_ID_KEY);
        if (key == null || StringUtils.isEmpty(key)) {
            return new WebAuthenticationDetails(context);
        } else {
            return new CustomWebAuthenticationDetails(context);
        }
    }
}
