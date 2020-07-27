package org.springframework.security.oauth.samples.configproperties;

public class Loginform {
    /**
     * 默认的登录页面url
     */
    private String loginPageUrl = "/";
    /**
     * 登录错误调转页面url
     */
    private String loginErrorPageUrl = "/";
    /**
     * 登录成功默认调转页面url
     */
    private String loginDefaultSucessUrl = "/";

    /**
     * 默认登录时,提交登录信息,服务进行处理的url
     * 只是用户密码，或者手机号码短信登录
     */
    private String defaultLoginPostUrl = "/login";

    public String getLoginPageUrl() {
        return loginPageUrl;
    }

    public void setLoginPageUrl(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }

    public String getLoginErrorPageUrl() {
        return loginErrorPageUrl;
    }

    public void setLoginErrorPageUrl(String loginErrorPageUrl) {
        this.loginErrorPageUrl = loginErrorPageUrl;
    }

    public String getLoginDefaultSucessUrl() {
        return loginDefaultSucessUrl;
    }

    public void setLoginDefaultSucessUrl(String loginDefaultSucessUrl) {
        this.loginDefaultSucessUrl = loginDefaultSucessUrl;
    }

    public String getDefaultLoginPostUrl() {
        return defaultLoginPostUrl;
    }

    public void setDefaultLoginPostUrl(String defaultLoginPostUrl) {
        this.defaultLoginPostUrl = defaultLoginPostUrl;
    }
}
