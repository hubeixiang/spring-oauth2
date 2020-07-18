package org.springframework.security.oauth.samples.custom;

import org.framework.hsven.i18n.I18nMessageUtil;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 如果设置的session并发策略为一个账户第二次登陆会将第一次给踢下来
 * 则第一次登陆的用户再访问我们的项目时会进入到该类
 * event里封装了request、response信息
 */
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletResponse response = event.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        String message = I18nMessageUtil.getInstance().getRequestMessage("CustomSessionInformationExpiredStrategy.onExpiredSessionDetected");
        response.getWriter().write(message);
    }

//    protected Locale getDefaultLocale() {
//        return (this.defaultLocale != null ? this.defaultLocale : LocaleContextHolder.getLocale());
//    }
}
