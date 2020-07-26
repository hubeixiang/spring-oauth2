package org.springframework.security.oauth.samples.custom.resourceserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * oauth2资源服务在登录时认证异常的自定义处理逻辑
 * 默认的返回样例如下:返回的是英文
 * {
 * "error":"unauthorized",
 * "error_description":"Full authentication is required to access this resource"
 * }
 */
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private static Logger logger = LoggerFactory.getLogger(CustomAuthExceptionHandler.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

//        Throwable cause = authException.getCause();
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        // CORS "pre-flight" request
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Cache-Control", "no-cache");
//        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//        response.addHeader("Access-Control-Max-Age", "1800");
//        if (cause instanceof InvalidTokenException) {
//            logger.error("InvalidTokenException : {}", cause.getMessage());
//            //Token无效
//            response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID)));
//        } else {
//            logger.error("AuthenticationException : NoAuthentication");
//            //资源未授权
//            response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.UNAUTHORIZED)));
//        }

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Cache-Control", "no-cache");
//        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//        response.addHeader("Access-Control-Max-Age", "1800");
//        //访问资源的用户权限不足
//        logger.error("AccessDeniedException : {}", accessDeniedException.getMessage());
//        response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.INSUFFICIENT_PERMISSIONS)));

    }
}
