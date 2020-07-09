package org.springframework.security.oauth.samples.web.url;

import org.springframework.util.StringUtils;

public class ImplicitUrl {
    private String httpPath;
    private String contextPath;
    private String redirect;
    private String oauthUrl = "oauth/authorize";
    private String grantType = "implicit";
    private CredentialsEntity cre = new CredentialsEntity();
    private MyUrl myUrl;

    public ImplicitUrl(String httpPath, String contextPath, String redirect) {
        this.httpPath = httpPath;
        this.contextPath = contextPath;
        this.redirect = redirect;
        stmyUrl();
    }

    private void stmyUrl() {
        //http://authserver:7090/oauth/authorize?response_type=token&client_id=messaging-client&scope=message.read message.write&grant_type=implicit&redirect_uri=http://webserver:7080/authorized
        String uri = null;
        if (StringUtils.isEmpty(contextPath)) {
            uri = String.format("%s/%s?%s&%s&%s&%s&%s", httpPath, oauthUrl
                    , cre.getParamGrantType(grantType)
                    , cre.getParamResponseType("token")
                    , cre.getParamScope(), cre.getParamClientId()
                    , cre.getParamRedirectUri(redirect));
        } else {
            uri = String.format("%s/%s/%s?%s&%s&%s&%s&%s", httpPath, contextPath, oauthUrl
                    , cre.getParamGrantType(grantType)
                    , cre.getParamResponseType("token")
                    , cre.getParamScope(), cre.getParamClientId()
                    , cre.getParamRedirectUri(redirect));
        }
        myUrl = new MyUrl();
        myUrl.setMethod("GET");
        myUrl.setUri(uri);
    }

    public MyUrl getMyUrl() {
        return myUrl;
    }
}