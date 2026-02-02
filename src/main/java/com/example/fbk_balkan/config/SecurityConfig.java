package com.example.fbk_balkan.config;

import com.example.fbk_balkan.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;   // Spring Boot auto-configures this from application.properties

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

//                        publicly accessible URLs
                                .requestMatchers("/", "/css/**", "/images/**", "/login", "/login-error").permitAll()
                                .requestMatchers("/trial-registration" , "/trial-registration-success" , "/about").permitAll()
                                .requestMatchers("/news", "/news/**").permitAll()

//                      roles-based access control
                                .requestMatchers("/coach/**").hasRole("COACH")
                                .requestMatchers("/admin/news/**").hasAnyRole("SOCIAL_ADMIN", "ADMIN")

//                      authentication for all other requests
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            String role = authentication.getAuthorities().iterator().next().getAuthority();
                            if (role.equals("ROLE_SOCIAL_ADMIN") || role.equals("ROLE_ADMIN")) {
                                response.sendRedirect("/admin/news");
                            } else {
                                response.sendRedirect("/coach/dashboard");
                            }
                        })
                        .failureUrl("/login-error")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("fbk-balkan-remember-me-secret-2026") // should be unique and secret
                        .tokenValiditySeconds(60 * 60 * 24 * 14) // 14 days

                        //.tokenValiditySeconds(120 ) 2min for testing

                        .rememberMeParameter("remember-me") // name of checkbox in login form
                        .tokenRepository(persistentTokenRepository())
                        .userDetailsService(userDetailsService)
                        .useSecureCookie(false)

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID" , "remember-me")
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        // Important: Only set to true ONCE (first startup) to auto-create the table.
        // After table exists → set to false or remove this line completely.
         tokenRepository.setCreateTableOnStartup(false);

        return tokenRepository;
    }

}
