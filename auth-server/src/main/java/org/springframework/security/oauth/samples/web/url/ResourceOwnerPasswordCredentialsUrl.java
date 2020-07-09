package org.springframework.security.oauth.samples.web.url;

import org.springframework.util.StringUtils;

public class ResourceOwnerPasswordCredentialsUrl {
    private String httpPath;
    private String contextPath;
    private String redirect;
    private String user;
    private String oauthUrl = "oauth/token";
    private String grantType = "password";
    private CredentialsEntity cre = new CredentialsEntity();
    private MyUrl myUrl;

    public ResourceOwnerPasswordCredentialsUrl(String httpPath, String contextPath, String redirect, String user) {
        this.httpPath = httpPath;
        this.contextPath = contextPath;
        this.redirect = redirect;
        this.user = user;
        stmyUrl();
    }

    private void stmyUrl() {
        //http://127.0.0.1:7090/oauth/token?&grant_type=password&scope=message.read message.write&username=user1&password=user1&client_id=message-client&client_secret=secret&redirect_uri=http://localhost:7081/authorized
        String uri = null;
        if (StringUtils.isEmpty(contextPath)) {
            uri = String.format("%s/%s?%s&%s&%s&%s&%s&%s", httpPath, oauthUrl
                    , cre.getParamGrantType(grantType)
                    , cre.getParamScope(), cre.getParamClient()
                    , cre.getParamVar("username", user), cre.getParamVar("password", user)
                    , cre.getParamRedirectUri(redirect));
        } else {
            uri = String.format("%s/%s/%s?%s&%s&%s&%s&%s&%s", httpPath, contextPath, oauthUrl
                    , cre.getParamGrantType(grantType)
                    , cre.getParamScope(), cre.getParamClient()
                    , cre.getParamVar("username", user), cre.getParamVar("password", user)
                    , cre.getParamRedirectUri(redirect));
        }
        myUrl = new MyUrl();
        myUrl.setMethod("PUT");
        myUrl.setUri(uri);
    }

    public MyUrl getMyUrl() {
        return myUrl;
    }
}
