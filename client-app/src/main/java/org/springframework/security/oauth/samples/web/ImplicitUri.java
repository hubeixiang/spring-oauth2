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
        implicitUri = String.format("%s?response_type=%s&client_id=%s&scope=%s&grant_type=%s&redirect_uri=%s",
                implicit.getUserAuthorizationUri(), "token",
                implicit.getClientId(), combine(implicit.getScope()), implicit.getGrantType(), implicit.getPreEstablishedRedirectUri());
        return implicitUri;
    }

    public String refreshTokenUri() {
        String refreshTokenUri = "";
        ImplicitResourceDetails implicit = (ImplicitResourceDetails) messagingClientImplicitDetails;

//        http://authserver:7090/oauth/token?
//        grant_type=refresh_token
//                &client_id=messaging-client
//                &refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZXa0ZDZnZ0dmxVQUxIODlkZVdHQ2JwNGhKb1NaQkJhZzlpYVlqUzFwVzA9In0.eyJleHAiOjE1OTM3MjA5MDAsInVzZXJfbmFtZSI6InVzZXIxIiwianRpIjoiMDI4MWJiNzMtYTQ1ZC00NjAxLWJkMGYtNTQzYTJiZWNiZDNhIiwiY2xpZW50X2lkIjoibWVzc2FnaW5nLWNsaWVudCIsInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXX0.HXeYP-uMelSVZ_Zu6JiZvTSDFYXTHo3cebwK2tyx4qmcTZJWpsxjNZMIwqUFjZCnKTr3zBKU9N7O5_9ZgBbWaHRIb1lXkTsC2RLne9CwIqL-wcQ2t3bPwhM4KShNHNH7xqYD01ZIkvDIOmrbSuMT291FD6T0xJdJQeB0Tus78jB2HEma9YcO4-eKoEZeDa0YsaJxHpmex_MJu6F8tmlE5gw8LkFLFBxLkDVD-8iXAh_JsDS0lLg-4oKZo7j0U3Z6cV5vAQV2UT3r8dmJbv-sEsJHB_wGnuZSna4YMAHpHm3gqIp-1SQX3WYLsLZMNViM4sfbgh_ApAyWIE4wuql59A
        refreshTokenUri = String.format("%s?grant_typ=%s&client_id=%s&refresh_token=%s",
                implicit.getAccessTokenUri(), "refresh_token",
                implicit.getClientId(), "");
        return refreshTokenUri;
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
