package org.springframework.security.oauth.samples.custom;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth.cache.commons.entity.StringCacheEntity;
import org.springframework.security.oauth.samples.cache.RedisUtil;
import org.springframework.security.oauth.samples.web.util.RSAUtils;
import org.springframework.util.StringUtils;

/**
 * 自定义的身份认证,主要添加前端页面加密后的密码进行解密
 */
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        Object details = authentication.getDetails();
        if (details != null && details instanceof CustomWebAuthenticationDetails) {
            //如果details不为空,并且是自定义web验证对象,表明密码需要用公钥进行解密
            CustomWebAuthenticationDetails customWebAuthenticationDetails = (CustomWebAuthenticationDetails) details;
            String key = ((CustomWebAuthenticationDetails) details).getPublickeyValue();
            StringCacheEntity value = RedisUtil.getString(RSAUtils.CACHE_KEY_PREFIX + key);
            if (value != null && !StringUtils.isEmpty(value.getCacheValue())) {
                presentedPassword = RSAUtils.decryptDataOnJava(presentedPassword, value.getCacheValue());
            }
            RedisUtil.remove(RSAUtils.CACHE_KEY_PREFIX + key);
        }

        if (!getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }
}
