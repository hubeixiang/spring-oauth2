package org.springframework.security.oauth.samples.custom.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.samples.web.util.ApiServiceConstants;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AuthenticationEntryPoint 默认实现是 LoginUrlAuthenticationEntryPoint, 该类的处理是转发或重定向到登录页面
 * 我们自定义的不同地方在于转发或者重定向时将要转发或者重新的源地址附带到登陆地址后面
 */
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    private RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    protected String determineUrlToUseForThisRequest(HttpServletRequest request,
                                                     HttpServletResponse response, AuthenticationException exception) {

        String defaultLoginFormUrl = getLoginFormUrl();
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            return defaultLoginFormUrl;
        }
        String redirectUrl = savedRequest.getRedirectUrl();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(redirectUrl)) {
            defaultLoginFormUrl = String.format("%s?%s=%s", defaultLoginFormUrl, ApiServiceConstants.IFRAME_SAVE_REQUST_LOGIN_URL_WEB_ID, redirectUrl);
        }
        return defaultLoginFormUrl;
    }
}
