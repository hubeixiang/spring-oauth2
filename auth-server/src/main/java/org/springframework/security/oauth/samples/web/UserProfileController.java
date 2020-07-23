/*
 *
 */

package org.springframework.security.oauth.samples.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.framework.hsven.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth.commons.entity.user.CustomUser;
import org.springframework.security.oauth.samples.configproperties.LoginConfigProperties;
import org.springframework.security.oauth.samples.web.util.ApiServiceConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller returns current authenticated user.
 *
 * @author
 * @since 1.0.0
 */
@RestController("profileController")
public class UserProfileController implements ApiEndpoint {

    private static final Logger logger = LoggerFactory
            .getLogger(UserProfileController.class);

    @Autowired
    private LoginConfigProperties loginConfigProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("hiosMobileUserDetailsService")
    private UserDetailsService hiosMobileUserDetailsService;

    private static void logout(final HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @GetMapping(path = {ApiServiceConstants.BASE_API_URL + "/user","/user"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object user(final HttpServletRequest request,
                       final HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logout(request);
            throw new AuthenticationServiceException(String.format("Missing [%s]", "Authentication"));
        }

        if (authentication.getPrincipal() == null) {
            logout(request);
            throw new AuthenticationServiceException(String.format("Missing [%s]", "Authentication Principal"));
        }
        String userId = getUserId(authentication);
        Date loginTime = getLoginTime(authentication);
        logger.debug("User userId is [{}]", userId);

        Object currentUser = null;
        try {
            if (StringUtils.isEmpty(loginConfigProperties.getUserProfile().getUserUri())) {
                currentUser = hiosMobileUserDetailsService.loadUserByUsername(userId);
            } else {
                String uri = String.format("%s/%s", loginConfigProperties.getUserProfile().getUserUri(), userId);
                currentUser = restTemplate.getForObject(uri, Object.class);
            }
            if (currentUser != null) {
                String json = JsonUtils.toJson(currentUser);
                Map tmp = JsonUtils.fromJson(json, Map.class);
                Map map = new HashMap();
                map.put("loginTime", loginTime);
                map.putAll(tmp);
                currentUser = map;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to retrieve user profile", e);
        }

        if (currentUser == null) {
            throw new RuntimeException("Failed to retrieve user profile.");
        }

        logger.debug("Final user profile is [{}]", JsonUtils.toJson(currentUser));
        return currentUser;
    }

    @GetMapping(path = {ApiServiceConstants.BASE_API_URL + "/status","/status"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserStatus status(final HttpServletRequest request,
                             final HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logout(request);
            return new UserStatus(false,
                    String.format("Missing [%s]", "Authentication"));
        }
        if (authentication.getPrincipal() == null) {
            logout(request);
            return new UserStatus(false,
                    String.format("Missing [%s]", "Authentication Principal"));
        }
        String userId = getUserId(authentication);
        return new UserStatus(true,
                String.format("Current user is: [%s]", userId));
    }

    private String getUserId(Authentication authentication) {
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

    private Date getLoginTime(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Date loginTime = null;
        if (principal instanceof CustomUser) {
            loginTime = ((CustomUser) principal).getLoginTime();
        } else {
            loginTime = new Date();
        }
        return loginTime;
    }


    private static class AccessTokenRequiredException extends AuthenticationException {

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

    private static class AccessTokenInvalidException extends AuthenticationException {

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

    private static class AccessTokenExpiredException extends AuthenticationException {

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
    private static class UserStatus {

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
