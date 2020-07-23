package org.springframework.security.oauth.samples.configproperties;

public class UserProfile {
    //访问用户管理服务的uri,查询指定用户的用户信息
    private String userUri = null;

    public String getUserUri() {
        return userUri;
    }

    public void setUserUri(String userUri) {
        this.userUri = userUri;
    }
}
