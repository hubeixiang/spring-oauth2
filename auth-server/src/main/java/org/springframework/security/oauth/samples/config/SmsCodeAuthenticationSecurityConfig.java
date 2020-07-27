package org.springframework.security.oauth.samples.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth.samples.configproperties.LoginConfigProperties;
import org.springframework.security.oauth.samples.custom.CustomAuthenticationSuccessHandler;
import org.springframework.security.oauth.samples.custom.sms.SmsCodeAuthenticationFilter;
import org.springframework.security.oauth.samples.custom.sms.SmsCodeAuthenticationProvider;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    @Qualifier("hiosMobileUserDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    private LoginConfigProperties loginConfigProperties;

    private AuthenticationSuccessHandler authenticationSuccessHandler = null;

    private SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        if (authenticationSuccessHandler == null) {
            authenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        }

        authenticationFailureHandler.setDefaultFailureUrl(loginConfigProperties.getLoginform().getLoginErrorPageUrl());

        AntPathRequestMatcher requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(loginConfigProperties.getSms().getSmsLoginPostUrl(), "POST");
        smsCodeAuthenticationFilter.setRequiresAuthenticationRequestMatcher(requiresAuthenticationRequestMatcher);

        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            smsCodeAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public SmsCodeAuthenticationSecurityConfig successHandler(AuthenticationSuccessHandler successHandler) {
        this.authenticationSuccessHandler = successHandler;
        return this;
    }


}
