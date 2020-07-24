package org.springframework.security.oauth.samples.custom.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth.commons.entity.user.CustomUser;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 与默认相比,如果是自定的用户信息,新增加一个user_id属性
 */
public class CustomDefaultUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    private String USER_ID = "user_id";

    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUser) {
            Map<String, Object> response = new LinkedHashMap<String, Object>();
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            response.put(USERNAME, authentication.getName());
            response.put(USER_ID, customUser.getUserId());
            if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
            }
            return response;
        } else {
            return super.convertUserAuthentication(authentication);
        }
    }

    public Authentication extractAuthentication(Map<String, ?> map) {
        Authentication authentication = super.extractAuthentication(map);

        if (authentication != null && map.containsKey(USER_ID)) {
            Object principal = map.get(USER_ID);
            return new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), authentication.getAuthorities());
        }
        return null;
    }
}
