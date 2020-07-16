/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.oauth.samples.custom.password.encoder;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth.samples.custom.password.custom.BssPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for creating {@link PasswordEncoder} instances
 *
 * @author Rob Winch
 * @since 5.0
 */
public class CustomPasswordEncoderFactories {
    private static final Map<String, PasswordEncoder> idToPasswordEncoder = new HashMap<>();
    private static CustomPasswordEncoderFactories instance;


    private CustomPasswordEncoderFactories() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("MD4", new org.springframework.security.crypto.password.Md4PasswordEncoder());
        encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        encoders.put("argon2", new Argon2PasswordEncoder());
        //自定义的
        encoders.put("bss", new BssPasswordEncoder());
        idToPasswordEncoder.putAll(encoders);
    }

    public synchronized static CustomPasswordEncoderFactories getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new CustomPasswordEncoderFactories();
        }
    }

    public PasswordEncoder getPasswordEncoder(String encoderType) {
        PasswordEncoder passwordEncoder = idToPasswordEncoder.get(encoderType);
        if (passwordEncoder == null) {
            passwordEncoder = idToPasswordEncoder.get("noop");
        }
        return passwordEncoder;
    }
}
