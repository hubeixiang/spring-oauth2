package org.springframework.security.oauth.commons.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.commons.dao.AuthoritiesDao;
import org.springframework.security.oauth.commons.dao.UserDao;
import org.springframework.security.oauth.commons.entity.user.CustomUser;
import org.springframework.security.oauth.commons.entity.user.SecurityUser;

import java.util.List;

public abstract class HiosUserDetail {
    public abstract UserDao getUserDao();

    public abstract AuthoritiesDao getAuthoritiesDao();

    public List<GrantedAuthority> loadUserAuthorities(String username) {
        return getAuthoritiesDao().loadUserAuthorities(username);
    }

    protected UserDetails createUserDetails(String username,
                                            SecurityUser securityUser, List<GrantedAuthority> combinedAuthorities) {
        String userId = securityUser.getUserId();
        String returnUsername = securityUser.getUsername();

        return new CustomUser(userId, returnUsername, securityUser.getPassword(),
                securityUser.isEnabled(), securityUser.isAccountNonExpired(),
                securityUser.isCredentialsNonExpired(), securityUser.isAccountNonLocked(), combinedAuthorities);
    }
}
