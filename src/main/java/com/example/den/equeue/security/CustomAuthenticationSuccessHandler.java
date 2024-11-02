package com.example.den.equeue.security;

import com.example.den.equeue.model.User;
import com.example.den.equeue.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws IOException, ServletException {
        final OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        final String id = oAuth2User.getAttribute("id").toString();
        final String username = oAuth2User.getAttribute("login").toString();

        log.info("User {} with id {} logged in successfully", username, id);
        final User user = userRepository.findById(id).orElseThrow();

        final  User newUser = new User(id, username);
        userRepository.save(newUser);

        response.sendRedirect("/");
    }
}
