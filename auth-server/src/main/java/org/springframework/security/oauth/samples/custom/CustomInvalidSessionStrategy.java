package org.springframework.security.oauth.samples.custom;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * session无效处理方法
 */
public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 自定义session无效处理
        System.out.println("---------------CustomInvalidSessionStrategy");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().append(messages.getMessage("CustomInvalidSessionStrategy.onExpiredSessionDetected"));
    }
}
