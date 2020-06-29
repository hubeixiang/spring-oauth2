package org.springframework.security.oauth.commons.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthoritiesDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GrantedAuthority> loadUserAuthorities(String username) {
        return Collections.emptyList();
    }
}
