/*
 *
 */

package org.springframework.security.oauth.samples.web;

import org.framework.hsven.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth.samples.configproperties.LoginConfigProperties;
import org.springframework.security.oauth.samples.web.util.ApiServiceConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller returns current authenticated user.
 *
 * @author
 * @since 1.0.0
 */
@RestController("userProfileAuthorizationController")
public class UserProfileAuthorizationController extends UserProfileController implements ApiEndpoint {

    private static final Logger logger = LoggerFactory
            .getLogger(UserProfileAuthorizationController.class);

    @Autowired
    private LoginConfigProperties loginConfigProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("hiosMobileUserDetailsService")
    private UserDetailsService hiosMobileUserDetailsService;

    @Autowired
    private SessionRegistry sessionRegistry;

    public UserProfileAuthorizationController() {
    }

    private static void logout(final HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @GetMapping(path = {ApiServiceConstants.BASE_API_URL + "/online/users/count", "/oauth2/online/users/count"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object online(final HttpServletRequest request,
                         final HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logout(request);
            throw new AuthenticationServiceException(String.format("Missing [%s]", "Authentication"));
        }

        if (authentication.getPrincipal() == null) {
            logout(request);
            throw new AuthenticationServiceException(String.format("Missing [%s]", "Authentication Principal"));
        }
        Map<String, Integer> map = new HashMap<>();
        List<Object> users = sessionRegistry.getAllPrincipals();
        int size = users.size();
        map.put("count", size);
        return map;
    }

    @GetMapping(path = {ApiServiceConstants.BASE_API_URL + "/user", "/oauth2/user"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
            currentUser = getCurrentUser(authentication, loginTime);
        } catch (RuntimeException e) {
            logger.error(String.format("UserProfileAuthorizationController.getCurrentUser(%s) Exception:%s", authentication), e);
            throw new RuntimeException("Failed to retrieve user profile", e);
        }

        if (currentUser == null) {
            throw new RuntimeException("Failed to retrieve user profile.");
        }

        logger.debug("Final user profile is [{}]", JsonUtils.toJson(currentUser));
        return currentUser;
    }

    @GetMapping(path = {ApiServiceConstants.BASE_API_URL + "/status", "/oauth2/status"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @Override
    public LoginConfigProperties getLoginConfigProperties() {
        return loginConfigProperties;
    }

    @Override
    public UserDetailsService getHiosMobileUserDetailsService() {
        return hiosMobileUserDetailsService;
    }

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
