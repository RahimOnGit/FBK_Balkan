package com.example.fbk_balkan.security;

import com.example.fbk_balkan.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Reset brute-force counter on successful login
        loginAttemptService.loginSucceeded(authentication.getName());

        // Role-based redirect (mirrors the original SecurityConfig logic)
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String target = switch (role) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_COACH" -> "/coach/dashboard";
            case "ROLE_SOCIAL_ADMIN" -> "/socialadmin/dashboard";
            default -> "/";
        };

        response.sendRedirect(target);
    }
}
