package org.springframework.security.oauth.samples.configproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "login")
public class LoginConfigProperties {
    //oauth登录默认使用的clientid
    private OauthClient oauthClient = new OauthClient();
    private Captcha captcha = new Captcha();
    private Password password = new Password();
    private Sms sms = new Sms();
    private UserProfile userProfile = new UserProfile();

    public OauthClient getOauthClient() {
        return oauthClient;
    }

    public void setOauthClient(OauthClient oauthClient) {
        this.oauthClient = oauthClient;
    }

    public Captcha getCaptcha() {
        return captcha;
    }

    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
