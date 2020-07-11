package org.springframework.security.oauth.samples.web.url;

public class CredentialsEntity {
    private String clientIdKey = "client_id";
    private String clientSecretKey = "client_secret";
    private String scopeKey = "scope";
    private String grantTypeKey = "grant_type";
    private String redirectUriKey = "redirect_uri";
    private String responseTypeKey = "response_type";
    //    private String clientId = "messaging-client";
    private String clientId = System.getProperty("clientId", "messaging-client");
    private String clientSecret = "secret";
    private String scope = "message.read message.write";

    public String getParamClient() {
        return String.format("%s&%s", getParamClientId(), getParamClientSecret());
    }

    public String getParamClientId() {
        return String.format("%s=%s", clientIdKey, clientId);
    }

    public String getParamClientSecret() {
        return String.format("%s=%s", clientSecretKey, clientSecret);
    }

    public String getParamScope() {
        return String.format("%s=%s", scopeKey, scope);
    }

    public String getParamGrantType(String type) {
        return String.format("%s=%s", grantTypeKey, type);
    }

    public String getParamRedirectUri(String redirect) {
        return String.format("%s=%s", redirectUriKey, redirect);
    }

    public String getParamResponseType(String responseType) {
        return String.format("%s=%s", responseTypeKey, responseType);
    }

    public String getParamVar(String key, String value) {
        return String.format("%s=%s", key, value);
    }

}
