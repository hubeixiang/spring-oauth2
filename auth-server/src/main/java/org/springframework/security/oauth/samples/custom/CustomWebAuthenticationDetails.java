package org.springframework.security.oauth.samples.custom;

import org.springframework.security.oauth.samples.web.util.RSAUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 页面请求的的参数中包含了公钥信息,以及验证图片记录
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private String publickeyValue = null;

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        publickeyValue = request.getParameter(RSAUtils.WEB_HTML_ID_KEY);
    }

    public String getPublickeyValue() {
        return publickeyValue;
    }
}
