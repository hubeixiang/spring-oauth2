package org.springframework.security.oauth.samples.custom.filter;

import org.framework.hsven.i18n.I18nMessageUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.cache.commons.entity.StringCacheEntity;
import org.springframework.security.oauth.samples.cache.RedisUtil;
import org.springframework.security.oauth.samples.web.util.SmsVerifyCodeUtil;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 短信验证码验证过滤
 */
@Component
public class SmsCodeFilter extends GenericFilterBean {
    private SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
    private String defaultFilterProcessUrl = "/login-mobile";
    private String defaultFailureUrl = "/login-error";

    public SmsCodeFilter() {
        simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl(defaultFailureUrl);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            // 验证码验证
            try {
                validate(new ServletWebRequest(request));
            } catch (AuthenticationException e) {
                simpleUrlAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), SmsVerifyCodeUtil.WEB_HTML_ID_KEY);
        if (!StringUtils.hasText(codeInRequest)) {
            throw new AuthenticationServiceException(getMessage("SmsCodeFilter.sms_code_not_empty"));
        }
        String mobile = ServletRequestUtils.getStringParameter(request.getRequest(), SmsVerifyCodeUtil.WEB_HTML_MOBILE_KEY);
        String key = String.format("%s%s_%s", SmsVerifyCodeUtil.CACHE_KEY_PREFIX, mobile, codeInRequest.toLowerCase());
        StringCacheEntity codeInSession = RedisUtil.getString(key);
        RedisUtil.remove(SmsVerifyCodeUtil.CACHE_KEY_PREFIX + codeInRequest.toLowerCase());
        if (codeInSession == null) {
            throw new AuthenticationServiceException(getMessage("SmsCodeFilter.sms_code_not_exists"));
        }
        if (codeInSession.isExpired()) {
            throw new AuthenticationServiceException(getMessage("SmsCodeFilter.sms_code_expired"));
        }
        if (!codeInSession.getCacheValue().equalsIgnoreCase(codeInRequest)) {
            throw new AuthenticationServiceException(getMessage("SmsCodeFilter.sms_code_error"));
        }
    }

    private String getMessage(String code) {
        return I18nMessageUtil.getInstance().getRequestMessage(code);
    }
}