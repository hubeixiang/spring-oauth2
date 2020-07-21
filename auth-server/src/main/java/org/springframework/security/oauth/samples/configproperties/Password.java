package org.springframework.security.oauth.samples.configproperties;

public class Password {
    //登录页面提交的密码是否需要加密
    private boolean jsencrypt = true;
    private Encoder encoder = new Encoder();

    public boolean isJsencrypt() {
        return jsencrypt;
    }

    public void setJsencrypt(boolean jsencrypt) {
        this.jsencrypt = jsencrypt;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }
}
