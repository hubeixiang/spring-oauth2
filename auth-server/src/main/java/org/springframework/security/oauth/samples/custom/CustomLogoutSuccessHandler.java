package org.springframework.security.oauth.samples.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth.commons.entity.user.CustomUser;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 登出成功处理
 */
@Component("customLogoutSuccessHandler")
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomLogoutSuccessHandler() {
        //登出成功时重定向的url
        setDefaultTargetUrl("/login?logout");
        //退出时给定了退出调转的地址
        setTargetUrlParameter("service");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws ServletException, IOException {
        // 登录成功后，进行数据处理
        if (authentication != null) {
            System.out.println("用户登出成功啦");
            String authenticationStr = objectMapper.writeValueAsString(authentication);
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                authenticationStr = "用户密码登录:" + authenticationStr;
            }
            System.out.println("用户登出信息打印：" + authenticationStr);

            deleteSessionRegistry(authentication);
        } else {
            System.out.println("session超时用户登出");
        }

        super.onLogoutSuccess(request, response, authentication);
    }

    private void publishEventDestory(HttpServletRequest request) {
        //SecurityContextLogoutHandler 如果配置了invalidateHttpSession后,获取不到当前的http session
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            HttpSessionDestroyedEvent e = new HttpSessionDestroyedEvent(httpSession);
            applicationContext.publishEvent(httpSession);
        }
    }

    private void deleteSessionRegistry(Authentication authentication) {
        //退出成功后删除当前用户session
        List<Object> o = sessionRegistry.getAllPrincipals();
        Object authPrincipal = authentication.getPrincipal();
        boolean isCustomUser = false;
        CustomUser authCustomUser = null;
        if (authPrincipal instanceof CustomUser) {
            isCustomUser = true;
            authCustomUser = (CustomUser) authPrincipal;
        }
        for (Object principal : o) {
            if (principal instanceof User) {
                final User loggedUser = (User) principal;
                if (loggedUser instanceof CustomUser && isCustomUser) {
                    CustomUser customUser = (CustomUser) loggedUser;
                    if (authCustomUser.getUserId().equals(customUser.getUserId())) {
                        delete(principal);
                    }
                } else {
                    if (authentication.getName().equals(loggedUser.getUsername())) {
                        delete(principal);
                    }
                }
            }
        }
    }

    private void delete(Object principal) {
        List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, true);
        if (null != sessionsInfo && sessionsInfo.size() > 0) {
            for (SessionInformation sessionInformation : sessionsInfo) {
                sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
            }
        }
    }
}