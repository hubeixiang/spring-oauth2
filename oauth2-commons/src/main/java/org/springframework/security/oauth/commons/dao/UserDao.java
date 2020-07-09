package org.springframework.security.oauth.commons.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth.commons.entity.user.SecUser;
import org.springframework.security.oauth.commons.entity.user.SecurityUser;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class UserDao {
    private final static String user_sql = "select * from sec_user where user_name= '%s'";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SecUser secUser = jdbcTemplate.query(String.format(user_sql, username), new ResultSetExtractor<SecUser>() {
            @Override
            public SecUser extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs != null && rs.next()) {
                    SecUser secUser = new SecUser();
                    secUser.setUserId(rs.getString("user_id".toUpperCase()));
                    secUser.setLoginId(rs.getString("login_id".toUpperCase()));
                    secUser.setUserName(rs.getString("user_name".toUpperCase()));
                    secUser.setUserStatus(rs.getInt("user_status".toUpperCase()));
//                    {
//                        java.sql.Date date = rs.getDate("deleted_time".toUpperCase());
//                        Instant instant = Instant.ofEpochSecond(date.getTime());
//                        secUser.setDeletedTime(instant);
//                    }
                    secUser.setPassword(rs.getString("password".toUpperCase()));
                    secUser.setPasswordRaw(rs.getString("password_raw".toUpperCase()));
//                    {
//                        java.sql.Date date = rs.getDate("create_time".toUpperCase());
//                        Instant instant = Instant.ofEpochSecond(date.getTime());
//                        secUser.setCreateTime(instant);
//                    }
//                    {
//                        java.sql.Date date = rs.getDate("modified_time".toUpperCase());
//                        Instant instant = Instant.ofEpochSecond(date.getTime());
//                        secUser.setModifiedTime(instant);
//                    }
                    return secUser;
                }
                return null;
            }
        });
        if (secUser == null) {
            throw new UsernameNotFoundException("username is not exists!");
        }
        secUser.setPassword(secUser.prefixPassword() + secUser.getPassword());
        return secUser;
    }
}
