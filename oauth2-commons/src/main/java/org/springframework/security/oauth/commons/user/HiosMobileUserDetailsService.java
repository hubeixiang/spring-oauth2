package org.springframework.security.oauth.commons.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth.commons.dao.AuthoritiesDao;
import org.springframework.security.oauth.commons.dao.UserDao;
import org.springframework.security.oauth.commons.entity.user.SecurityUser;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HiosMobileUserDetailsService extends HiosUserDetail implements UserDetailsService, MessageSourceAware {
    private static Logger logger = LoggerFactory.getLogger(HiosMobileUserDetailsService.class);
    private final UserDao userDao;
    private final AuthoritiesDao authoritiesDao;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = true;
    private boolean enableGroups;

    public HiosMobileUserDetailsService(UserDao userDao, AuthoritiesDao authoritiesDao) {
        this.userDao = userDao;
        this.authoritiesDao = authoritiesDao;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        SecurityUser securityUser = loadUsersByMobile(mobile);

        if (securityUser == null) {
            this.logger.debug("Query returned no results for user mobile '" + mobile + "'");

            throw new UsernameNotFoundException(
                    this.messages.getMessage("JdbcDaoImpl.notFound",
                            new Object[]{mobile}, "mobile {0} not found"));
        }

        Set<GrantedAuthority> dbAuthsSet = new HashSet<>();

        if (this.enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(securityUser.getUsername()));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);

        return createUserDetails(securityUser.getUsername(), securityUser, dbAuths);
    }

    private SecurityUser loadUsersByMobile(String mobile) {
        return userDao.loadUserByMobile(mobile);
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public AuthoritiesDao getAuthoritiesDao() {
        return authoritiesDao;
    }

}
