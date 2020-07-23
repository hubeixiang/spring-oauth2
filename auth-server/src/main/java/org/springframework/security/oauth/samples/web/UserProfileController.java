/*
 *
 */

package org.springframework.security.oauth.samples.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.framework.hsven.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth.commons.entity.user.CustomUser;
import org.springframework.security.oauth.samples.configproperties.LoginConfigProperties;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller returns current authenticated user.
 *
 * @author
 * @since 1.0.0
 */
public abstract class UserProfileController {
    private static final Logger logger = LoggerFactory
            .getLogger(UserProfileController.class);

    public abstract LoginConfigProperties getLoginConfigProperties();

    public abstract UserDetailsService getHiosMobileUserDetailsService();

    public abstract RestTemplate getRestTemplate();

    protected Object getCurrentUser(Authentication authentication, Date loginTime) {
        String userId = getUserId(authentication);
        Object currentUser = null;
        if (StringUtils.isEmpty(getLoginConfigProperties().getUserProfile().getUserUri())) {
            currentUser = getHiosMobileUserDetailsService().loadUserByUsername(userId);
        } else {
            String uri = String.format("%s/%s", getLoginConfigProperties().getUserProfile().getUserUri(), userId);
            currentUser = getRestTemplate().getForObject(uri, Object.class);
        }
        if (currentUser != null) {
            String json = JsonUtils.toJson(currentUser);
            Map tmp = JsonUtils.fromJson(json, Map.class);
            Map map = new HashMap();
            map.put("loginTime", loginTime);
            map.putAll(tmp);
            currentUser = map;
        }
        return currentUser;
    }

    protected String getUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String userId = null;
        if (principal instanceof CustomUser) {
            userId = ((CustomUser) principal).getUserId();
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            userId = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            userId = principal.toString();
        }
        return userId;
    }

    protected Date getLoginTime(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Date loginTime = null;
        if (principal instanceof CustomUser) {
            loginTime = ((CustomUser) principal).getLoginTime();
        } else {
            loginTime = new Date();
        }
        return loginTime;
    }


    protected static class AccessTokenRequiredException extends AuthenticationException {

        AccessTokenRequiredException() {
            super("Requires authentication");
        }

        public String getOAuth2ErrorCode() {
            return "missing_access_token"; // unauthorized
        }

        public int getHttpErrorCode() {
            return 401;
        }
    }

    protected static class AccessTokenInvalidException extends AuthenticationException {

        AccessTokenInvalidException() {
            super("Bad credentials");
        }

        public String getOAuth2ErrorCode() {
            return "invalid_access_token";
        }

        public int getHttpErrorCode() {
            return 401;
        }
    }

    protected static class AccessTokenExpiredException extends AuthenticationException {

        AccessTokenExpiredException() {
            super("Expired credentials");
        }

        public String getOAuth2ErrorCode() {
            return "expired_access_token";
        }

        public int getHttpErrorCode() {
            return 401;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected static class UserStatus {

        private final boolean authenticated;
        private final String message;
        private final Long expiresIn;

        UserStatus(boolean authenticated, String message) {
            this(authenticated, message, null);
        }

        UserStatus(boolean authenticated, Long expiresIn) {
            this(authenticated, null, expiresIn);
        }

        UserStatus(boolean authenticated, String message, Long expiresIn) {
            this.authenticated = authenticated;
            this.message = message;
            this.expiresIn = expiresIn;
        }

        public boolean isAuthenticated() {
            return this.authenticated;
        }

        public String getMessage() {
            return this.message;
        }

        public Long getExpiresIn() {
            return this.expiresIn;
        }
    }

}
