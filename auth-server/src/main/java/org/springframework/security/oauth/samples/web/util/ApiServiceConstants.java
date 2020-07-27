/*
 *
 */

package org.springframework.security.oauth.samples.web.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Constants for the API service.
 *
 * @author
 * @since 1.0.0
 */
public final class ApiServiceConstants {

    /**
     * Base API url.
     */
    public static final String BASE_API_URL = "/api/v1";

    public static final String IFRAME_SAVE_REQUST_LOGIN_URL_WEB_ID = "iframeSaveRequestLoginID";

    private ApiServiceConstants() {
    }

    public static final String parserIframeUrl(HttpServletRequest request) {
        String redirect = request.getParameter(ApiServiceConstants.IFRAME_SAVE_REQUST_LOGIN_URL_WEB_ID);
        if (StringUtils.isNotEmpty(redirect)) {
            StringBuffer redirectUrl = new StringBuffer();
            redirectUrl.append(redirect);
            Set<String> keys = request.getParameterMap().keySet();
            for (String key : keys) {
                if (key.equalsIgnoreCase(ApiServiceConstants.IFRAME_SAVE_REQUST_LOGIN_URL_WEB_ID)) {
                    continue;
                }
                if (redirectUrl.length() == 0) {
                    redirectUrl.append(key).append("=").append(request.getParameter(key));
                } else {
                    redirectUrl.append("&").append(key).append("=").append(request.getParameter(key));
                }
            }
            redirect = redirectUrl.toString();
        }
        return redirect;
    }
}
