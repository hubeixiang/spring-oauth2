/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth.samples.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth.commons.dao.AuthoritiesDao;
import org.springframework.security.oauth.commons.dao.UserDao;
import org.springframework.security.oauth.commons.user.HiosMobileUserDetailsService;
import org.springframework.security.oauth.commons.user.HiosUserDetailsService;
import org.springframework.security.oauth.samples.configproperties.LoginConfigProperties;
import org.springframework.security.oauth.samples.custom.CustomAuthenticationSuccessHandler;
import org.springframework.security.oauth.samples.custom.CustomDaoAuthenticationProvider;
import org.springframework.security.oauth.samples.custom.CustomInvalidSessionStrategy;
import org.springframework.security.oauth.samples.custom.CustomSessionInformationExpiredStrategy;
import org.springframework.security.oauth.samples.custom.CustomWebAuthenticationDetailsSource;
import org.springframework.security.oauth.samples.custom.filter.SmsCodeFilter;
import org.springframework.security.oauth.samples.custom.filter.VerifyCodeFilter;
import org.springframework.security.oauth.samples.custom.password.encoder.CustomPasswordEncoderFactories;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 验证服务器的安全控制实现
 *
 * @author Joe Grandja
 */
@EnableWebSecurity
public class CustomAuthorizationWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthoritiesDao authoritiesDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private VerifyCodeFilter validateCodeFilter;

    @Autowired
    @Qualifier("customLogoutSuccessHandler")
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private LoginConfigProperties loginConfigProperties;

    @Autowired
    private SmsCodeFilter smsCodeFilter;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                //定义不用验证的url
                .antMatchers("/oauth2/keys", "/favicon.ico", "/webjars/**", "/welcome", "/static/**", "/code/**").permitAll()
                //登录与登录失败调转url不用验证
                // 自定义页面的路径不用验证
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                // 失败跳转不用验证
                .antMatchers(HttpMethod.GET, "/login-error").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class); // 添加验证码校验过滤器
        http.addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class); // 添加短信验证码校验过滤器
        http.apply(smsCodeAuthenticationSecurityConfig);// 将短信验证码认证配置加到 Spring Security 中

        //session 管理
        http.sessionManagement()
//                .invalidSessionUrl("/session/invalid")
                .invalidSessionStrategy(new CustomInvalidSessionStrategy())
                // 设置同一个用户只能有一个登陆session
                .maximumSessions(1)
                // 设置为true，即禁止后面其它人的登录 ,不设置则是后登录导致前登录失效
                .maxSessionsPreventsLogin(false)
                //其他地方登录session失效处理策略
                .expiredSessionStrategy(new CustomSessionInformationExpiredStrategy())
                //设置自己的sessionRegistry
                .sessionRegistry(sessionRegistry());

//        http.sessionManagement().addObjectPostProcessor(new ObjectPostProcessor<ConcurrentSessionFilter>() {
//            @Override
//            public ConcurrentSessionFilter postProcess(ConcurrentSessionFilter object) {
//                sessionRegistry = object.get
//                return null;
//            }
//        });

        //定义登录操作
        http.formLogin()
                //定义解析登录时除了用户密码外的验证详细信息
                .authenticationDetailsSource(new CustomWebAuthenticationDetailsSource())
                //设置自定义的登录页面
                .loginPage("/login")
                //登录失败跳转，指定的路径要能匿名访问
                .failureUrl("/login-error")
                //登录成功重定向地址(与登录成功调转地址)
//                .successForwardUrl("/index");
                //登录成功后自定义的用户信息记录,比如记录登录用户,登录用户数等等
                .successHandler(new CustomAuthenticationSuccessHandler());
        //登出
        http.logout()
                //用户登出成功处理
                .logoutSuccessHandler(logoutSuccessHandler)
                //退出时session失效
                .invalidateHttpSession(true)
//                退出时删除指定的cookie
                .deleteCookies("JSESSIONID");


        //跨域,使用org.springframework.security.oauth.samples.custom.filter.CustomCorsFilter进行控制
        http.cors();
        http.csrf().disable();
    }
    // @formatter:on

    @Bean
    public UserDetailsService hiosUserDetailsService() {
        HiosUserDetailsService hiosUserDetailsService = new HiosUserDetailsService(userDao, authoritiesDao);
        return hiosUserDetailsService;
    }

    @Bean
    public UserDetailsService hiosMobileUserDetailsService() {
        HiosMobileUserDetailsService hiosMobileUserDetailsService = new HiosMobileUserDetailsService(userDao, authoritiesDao);
        return hiosMobileUserDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return CustomPasswordEncoderFactories.getInstance().getPasswordEncoder(loginConfigProperties.getPassword().getEncoder().getType());
    }

    @Bean
    public CustomDaoAuthenticationProvider customDaoAuthenticationProvider() {
        CustomDaoAuthenticationProvider customDaoAuthenticationProvider = new CustomDaoAuthenticationProvider();
        customDaoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        customDaoAuthenticationProvider.setUserDetailsService(hiosUserDetailsService());
        return customDaoAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customDaoAuthenticationProvider());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();
        registerDelegateApplicationListener(sessionRegistry);
        return sessionRegistry;
    }

    private void registerDelegateApplicationListener(
            ApplicationListener<?> delegate) {
        if (applicationContext.getBeansOfType(DelegatingApplicationListener.class).isEmpty()) {
            return;
        }
        DelegatingApplicationListener delegating = applicationContext
                .getBean(DelegatingApplicationListener.class);
        SmartApplicationListener smartListener = new GenericApplicationListenerAdapter(
                delegate);
        delegating.addListener(smartListener);
    }
}