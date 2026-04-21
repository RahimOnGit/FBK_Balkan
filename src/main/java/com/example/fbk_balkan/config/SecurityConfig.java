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
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

//                        publicly accessible URLs
                                .requestMatchers("/", "/css/**", "/images/**","/uploads/**", "/login", "/login-error").permitAll()
                                .requestMatchers("/trial-registration", "/trial-registration/success","/about").permitAll()
                                .requestMatchers("/kontakt").permitAll()
                                .requestMatchers("/trial-registration", "/trial-registration/success","/about","/faq").permitAll()
                                .requestMatchers("/news", "/news/**","/public-teams/**").permitAll()
                                .requestMatchers("/sponsors").permitAll()
                                .requestMatchers("/ungdomsportalen").permitAll()
                                .requestMatchers("/verksamhet").permitAll()


//                      roles-based access control
                                .requestMatchers("/coach/**").hasAnyRole("COACH", "ADMIN")
                                .requestMatchers("/socialadmin/**").hasRole("SOCIAL_ADMIN")
                                .requestMatchers("/admin/news/**").hasAnyRole("SOCIAL_ADMIN", "ADMIN")
                                .requestMatchers("/admin/dashboard", "/admin/teams", "/admin/age-groups", "/admin/coaches", "/admin/trials", "/admin/faqs/**")
                                .hasRole("ADMIN")
                                .requestMatchers("/team-register").hasRole("ADMIN")


                                // Role-based access control
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                // profile page (IMPORTANT)
                                .requestMatchers("/profile/**").authenticated()
                                // Authentication for all other requests
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            String role = authentication.getAuthorities().iterator().next().getAuthority();
                            if (role.equals("ROLE_ADMIN")) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (role.equals("ROLE_COACH")) {
                                response.sendRedirect("/coach/dashboard");
                            } else if (role.equals("ROLE_SOCIAL_ADMIN")) {
                                response.sendRedirect("/socialadmin/dashboard");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/login-error")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("fbk-balkan-remember-me-secret-2026")
                        .tokenValiditySeconds(60 * 60 * 24 * 14) // 14 days
                        .rememberMeParameter("remember-me")
                        .tokenRepository(persistentTokenRepository())
                        .userDetailsService(userDetailsService)
                        .useSecureCookie(false)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
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

//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);
//        return tokenRepository;
//    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        // Ensure the table exists manually using SQLite safe syntax
        // This avoids the 'Table already exists' crash on restarts
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            // SQLite specific "CREATE TABLE IF NOT EXISTS"
            String sql = "CREATE TABLE IF NOT EXISTS persistent_logins (" +
                    "username VARCHAR(64) NOT NULL, " +
                    "series VARCHAR(64) PRIMARY KEY, " +
                    "token VARCHAR(64) NOT NULL, " +
                    "last_used TIMESTAMP NOT NULL" +
                    ")";

            statement.execute(sql);
        } catch (Exception e) {
            // Log the error if needed, but don't crash the app
            System.err.println("Error ensuring persistent_logins table exists: " + e.getMessage());
        }

        //  We set this to FALSE because we handled the creation manually above.
        tokenRepository.setCreateTableOnStartup(false);

        return tokenRepository;
    }

}