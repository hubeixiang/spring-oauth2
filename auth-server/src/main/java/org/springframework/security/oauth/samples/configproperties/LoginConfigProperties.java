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

}
