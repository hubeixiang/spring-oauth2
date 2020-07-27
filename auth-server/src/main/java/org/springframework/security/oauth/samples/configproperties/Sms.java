package org.springframework.security.oauth.samples.configproperties;

public class Sms {
    //短信验证码登录时超时时间,0为不设置
    private int timeout = 0;
    /**
     * 手机号码短信登录时提交短信验证码并进行验证处理登录的url
     */
    private String smsLoginPostUrl = "/login-mobile";

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getSmsLoginPostUrl() {
        return smsLoginPostUrl;
    }

    public void setSmsLoginPostUrl(String smsLoginPostUrl) {
        this.smsLoginPostUrl = smsLoginPostUrl;
    }
}
