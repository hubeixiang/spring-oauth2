package org.springframework.security.oauth.commons.entity.user;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SecUser implements SecurityUser,Serializable {
    private String userId;
    private String loginId;
    private String userName;
    private Integer userStatus;
    private Instant deletedTime;
    private String password;
    private String passwordRaw;
    private Instant createTime;
    private Instant modifiedTime;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Boolean pwdMustChange;
    private LocalDate pwdExpireDate;
    private Integer loginFailedTimes;
    private Instant loginLockTime;
    private String userEmail;
    private String userMobile;
    private String userPhone;
    private String memo;

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Instant getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Instant deletedTime) {
        this.deletedTime = deletedTime;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String prefixPassword() {
        return "{noop}";
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRaw() {
        return passwordRaw;
    }

    public void setPasswordRaw(String passwordRaw) {
        this.passwordRaw = passwordRaw;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public LocalDateTime getValidStartTime() {
        return validStartTime;
    }

    public void setValidStartTime(LocalDateTime validStartTime) {
        this.validStartTime = validStartTime;
    }

    public LocalDateTime getValidEndTime() {
        return validEndTime;
    }

    public void setValidEndTime(LocalDateTime validEndTime) {
        this.validEndTime = validEndTime;
    }

    public Boolean getPwdMustChange() {
        return pwdMustChange;
    }

    public void setPwdMustChange(Boolean pwdMustChange) {
        this.pwdMustChange = pwdMustChange;
    }

    public LocalDate getPwdExpireDate() {
        return pwdExpireDate;
    }

    public void setPwdExpireDate(LocalDate pwdExpireDate) {
        this.pwdExpireDate = pwdExpireDate;
    }

    public Integer getLoginFailedTimes() {
        return loginFailedTimes;
    }

    public void setLoginFailedTimes(Integer loginFailedTimes) {
        this.loginFailedTimes = loginFailedTimes;
    }

    public Instant getLoginLockTime() {
        return loginLockTime;
    }

    public void setLoginLockTime(Instant loginLockTime) {
        this.loginLockTime = loginLockTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
