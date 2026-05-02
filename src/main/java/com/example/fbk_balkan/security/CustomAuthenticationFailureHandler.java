package com.example.fbk_balkan.security;

import com.example.fbk_balkan.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String email = request.getParameter("username");

        boolean lockedNow;
        if (exception instanceof LockedException) {
            // CustomUserDetailsService already saw the user as locked
            lockedNow = true;
        } else {
            lockedNow = loginAttemptService.loginFailed(email);
        }

        String redirect = lockedNow ? "/login?error=locked" : "/login?error=invalid";
        setDefaultFailureUrl(redirect);
        super.onAuthenticationFailure(request, response, exception);
    }
}
