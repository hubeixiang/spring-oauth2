package org.springframework.security.oauth.samples.custom.authentication;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.samples.web.util.ApiServiceConstants;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static Logger logger = LoggerFactory.getLogger(CustomSimpleUrlAuthenticationFailureHandler.class);
    private String defaultFailureUrl;

    public CustomSimpleUrlAuthenticationFailureHandler() {
    }

    public String getDefaultFailureUrl() {
        return defaultFailureUrl;
    }

    public void setDefaultFailureUrl(String defaultFailureUrl) {
        super.setDefaultFailureUrl(defaultFailureUrl);
        this.defaultFailureUrl = defaultFailureUrl;
    }

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if (defaultFailureUrl == null) {
            logger.debug("No failure URL set, sending 401 Unauthorized error");

            response.sendError(HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase());
        } else {
            saveException(request, exception);

            if (isUseForward()) {
                logger.debug("Forwarding to " + defaultFailureUrl);

                request.getRequestDispatcher(defaultFailureUrl)
                        .forward(request, response);
            } else {
                String redirectUrl = defaultFailureUrl;
                String iframeUrl = ApiServiceConstants.parserIframeUrl(request);
                if (StringUtils.isNotEmpty(iframeUrl)) {
                    redirectUrl = String.format("%s?%s=%s", redirectUrl, ApiServiceConstants.IFRAME_SAVE_REQUST_LOGIN_URL_WEB_ID, iframeUrl);
                }
                logger.debug("Redirecting to " + redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            }
        }
    }
}
