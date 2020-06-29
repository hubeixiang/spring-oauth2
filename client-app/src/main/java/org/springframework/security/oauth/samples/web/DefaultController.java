/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth.samples.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

/**
 * @author Joe Grandja
 */
@Controller
public class DefaultController {
    @Value("${messages.welcome-uri}")
    private String messagesWelcomeUri;

    @Autowired
    @Qualifier("messagingClientAuthCodeRestTemplate")
    private OAuth2RestTemplate messagingClientAuthCodeRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/welcome")
    public String wellcome(Model model) {
        String[] messages = this.restTemplate.getForObject(this.messagesWelcomeUri, String[].class);
        model.addAttribute("messages", messages);
        return "welcome";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return login();
    }
}