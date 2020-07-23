package org.springframework.security.oauth.commons.entity.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Date;

public class CustomUser extends User {
    private String userId;
    private String loginId;
    private Date loginTime = new Date();

    public CustomUser(String userId, String loginId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.loginId = loginId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("userId: ").append(this.userId).append("; ");
        sb.append("loginId: ").append(this.loginId).append("; ");
        sb.append("Username: ").append(this.getUsername()).append("; ");
        sb.append("loginTime: ").append(this.loginTime).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.isEnabled()).append("; ");
        sb.append("AccountNonExpired: ").append(this.isAccountNonExpired()).append("; ");
        sb.append("credentialsNonExpired: ").append(this.isCredentialsNonExpired())
                .append("; ");
        sb.append("AccountNonLocked: ").append(this.isAccountNonLocked()).append("; ");

        if (!getAuthorities().isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : getAuthorities()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }

    public String getLoginId() {
        return loginId;
    }

    public Date getLoginTime() {
        return loginTime;
    }
}
