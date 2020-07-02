package org.springframework.security.oauth.samples.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImplicitUri {
    @Autowired
    @Qualifier("messagingClientImplicitDetails")
    OAuth2ProtectedResourceDetails messagingClientImplicitDetails;

    public String generatorUri() {
        String implicitUri = "";
        ImplicitResourceDetails implicit = (ImplicitResourceDetails) messagingClientImplicitDetails;
        //http://authserver:7090/oauth/authorize?response_type=token&client_id=messaging-client&scope=message.read message.write&grant_type=implicit&redirect_uri=http://www.baidu.com
        implicitUri = String.format("%s?response_type=%s&client_id=%s&scope=%s&grant_type=%s&redirect_uri=%s", implicit.getAccessTokenUri(), "token",
                implicit.getClientId(), combine(implicit.getScope()), implicit.getGrantType(), implicit.getPreEstablishedRedirectUri());
        return implicitUri;
    }


    private String combine(List<String> scopes) {
        StringBuffer sb = new StringBuffer();
        for (String scope : scopes) {
            if (sb.length() == 0) {
                sb.append(scope);
            } else {
                sb.append(" ").append(scope);
            }
        }
        if (sb.length() == 0) {
            return "*";
        } else {
            return sb.toString();
        }
    }
}
