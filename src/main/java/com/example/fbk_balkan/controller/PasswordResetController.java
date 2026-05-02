package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // ---------- Forgot password (request a reset link) ----------

    @GetMapping("/forgot-password")
    public String showForgotForm(Model model) {
        model.addAttribute("success", false);
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam("email") String email,
                                HttpServletRequest request,
                                Model model) {
        String baseUrl = buildBaseUrl(request);
        passwordResetService.createAndSendResetToken(email.trim().toLowerCase(), baseUrl);
        // Generic response — never reveal whether the email exists
        model.addAttribute("success",
                "Om en e-postadress matchar har en återställningslänk skickats. Kontrollera din inkorg.");
        return "forgot-password";
    }

    // ---------- Reset password (use the token) ----------

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam(value = "token", required = false) String token,
                                Model model) {
        if (token == null || passwordResetService.findValidToken(token).isEmpty()) {
            model.addAttribute("error",
                    "Återställningslänken är ogiltig eller har gått ut. Begär en ny.");
            model.addAttribute("invalidToken", true);
            model.addAttribute("showForm", false);
        } else {
            model.addAttribute("token", token);
            model.addAttribute("invalidToken", false);
            model.addAttribute("showForm", true);
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processReset(@RequestParam("token") String token,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {
        if (password == null || password.length() < 8) {
            model.addAttribute("error", "Lösenordet måste vara minst 8 tecken.");
            model.addAttribute("token", token);
            model.addAttribute("invalidToken", false);
            model.addAttribute("showForm", true);
            return "reset-password";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Lösenorden matchar inte.");
            model.addAttribute("token", token);
            model.addAttribute("invalidToken", false);
            model.addAttribute("showForm", true);
            return "reset-password";
        }

        boolean ok = passwordResetService.resetPassword(token, password);
        if (!ok) {
            model.addAttribute("error",
                    "Återställningslänken är ogiltig eller har gått ut. Begär en ny.");
            model.addAttribute("invalidToken", true);
            model.addAttribute("showForm", false);
            return "reset-password";
        }

        model.addAttribute("success",
                "Ditt lösenord har uppdaterats. Du kan nu logga in.");
        model.addAttribute("invalidToken", false);
        model.addAttribute("showForm", false);
        return "reset-password";
    }

    private String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        // Honour X-Forwarded-Proto / X-Forwarded-Host for reverse-proxy / Replit deployments
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedHost  = request.getHeader("X-Forwarded-Host");
        if (forwardedProto != null && !forwardedProto.isBlank()) scheme = forwardedProto.trim();
        if (forwardedHost  != null && !forwardedHost.isBlank())  {
            serverName = forwardedHost.trim();
            port = -1; // host header already includes port if needed
        }
        StringBuilder sb = new StringBuilder(scheme).append("://").append(serverName);
        if (port > 0) {
            boolean isDefault = (port == 80 && "http".equals(scheme))
                    || (port == 443 && "https".equals(scheme));
            if (!isDefault) sb.append(":").append(port);
        }
        return sb.toString();
    }
}
