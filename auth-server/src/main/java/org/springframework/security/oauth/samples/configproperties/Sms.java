package org.springframework.security.oauth.samples.configproperties;

public class Sms {
    //短信验证码登录时超时时间,0为不设置
    private int timeout = 0;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
