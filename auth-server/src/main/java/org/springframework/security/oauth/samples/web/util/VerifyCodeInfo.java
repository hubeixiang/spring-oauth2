package org.springframework.security.oauth.samples.web.util;

import java.awt.image.BufferedImage;

public class VerifyCodeInfo {
    private BufferedImage bufferedImage;
    private String verifyCode;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
