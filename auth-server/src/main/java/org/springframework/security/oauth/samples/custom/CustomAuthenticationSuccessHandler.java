package org.springframework.security.oauth.samples.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录成功后处理
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {
        // 登录成功后，进行数据处理
        System.out.println("用户登录成功啦！！！");
        String authenticationStr = objectMapper.writeValueAsString(authentication);
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            authenticationStr = "用户密码登录:" + authenticationStr;
        }
        System.out.println("用户登录信息打印：" + authenticationStr);

        //处理完成后，跳转回原请求URL
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
