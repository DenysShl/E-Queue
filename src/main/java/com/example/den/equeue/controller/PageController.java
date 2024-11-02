package com.example.den.equeue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PageController {

    @GetMapping(value = "/", produces = "text/html")
    public ModelAndView get() {
        return new ModelAndView("index");
    }

    @GetMapping("/user")
    public Map<String, Object> getUserByOAuth(final @AuthenticationPrincipal OAuth2User principal) {
        return Map.of("username", principal.getAttribute("login"));

    }
}
