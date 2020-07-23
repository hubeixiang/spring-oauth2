package org.springframework.security.oauth.samples.custom;

import org.apache.commons.lang3.StringUtils;
import org.framework.hsven.i18n.I18nMessageUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * session无效处理方法
 */
public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {
    String failurePage = "/login-error";
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 自定义session无效处理
        System.out.println("---------------CustomInvalidSessionStrategy");
//        response.setContentType("application/json;charset=UTF-8");
        String message = I18nMessageUtil.getInstance().getRequestMessage("CustomInvalidSessionStrategy.onExpiredSessionDetected");
//        response.getWriter().append(message);
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new AuthenticationServiceException(message));
//        response.sendRedirect(location);
        String redirect_uri = null;
        redirect_uri = getRequestUri(request);
        redirectStrategy.sendRedirect(request, response, redirect_uri);
//        response.getWriter().write("<script>window.parent.location.href = 'default';</script>");
    }

    private String getRequestUri(HttpServletRequest request) {
        String method = request.getMethod();
        if (method.equalsIgnoreCase("get")) {
            String uri = request.getRequestURI();
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuffer sb = new StringBuffer();
            for (String key : parameterMap.keySet()) {
                String value = request.getParameter(key);
                if (StringUtils.isNotEmpty(value)) {
                    if (sb.length() == 0) {
                        sb.append(key).append("=").append(value);
                    } else {
                        sb.append("&").append(key).append("=").append(value);
                    }
                }
            }
            if (sb.length() != 0) {
                uri = uri + "?" + sb.toString();
            }
            return uri;
        } else {
            return failurePage;
        }
    }
}
