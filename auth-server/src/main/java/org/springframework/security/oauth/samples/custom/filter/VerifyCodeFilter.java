package org.springframework.security.oauth.samples.custom.filter;

import org.framework.hsven.i18n.I18nMessageUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.samples.cache.RedisUtil;
import org.springframework.security.oauth.samples.web.util.VerifyCodeUtil;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码登录时的验证处理
 */
@Component
public class VerifyCodeFilter extends GenericFilterBean {
    private SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
    private String defaultFilterProcessUrl = "/login";
    private String defaultFailureUrl = "/login-error";

    public VerifyCodeFilter() {
        simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl(defaultFailureUrl);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            // 验证码验证
            try {
                String requestCaptcha = request.getParameter(VerifyCodeUtil.WEB_HTML_ID_KEY);
                if (StringUtils.isEmpty(requestCaptcha))
                    throw new AuthenticationServiceException(getMessage("WelcomeLoginController.verifyCode_notEmpty"));
                String genCaptcha = RedisUtil.get(VerifyCodeUtil.CACHE_KEY_PREFIX + requestCaptcha.toLowerCase());
                RedisUtil.remove(VerifyCodeUtil.CACHE_KEY_PREFIX + requestCaptcha.toLowerCase());
                if (StringUtils.isEmpty(genCaptcha) || !genCaptcha.toLowerCase().equals(requestCaptcha.toLowerCase())) {
                    throw new AuthenticationServiceException(getMessage("WelcomeLoginController.verifyCode_ERROR"));
                }
            } catch (AuthenticationException e) {
                simpleUrlAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String getMessage(String code) {
        return I18nMessageUtil.getInstance().getRequestMessage(code);
    }
}
