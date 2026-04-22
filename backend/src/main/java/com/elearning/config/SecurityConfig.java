package com.elearning.config;

import com.elearning.security.CustomAuthEntryPoint;
import com.elearning.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration that defines the HTTP security filter chain,
 * endpoint authorization rules, password encoding, and authentication management.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthEntryPoint authEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter, CustomAuthEntryPoint authEntryPoint,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.authEntryPoint = authEntryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Configures the security filter chain with stateless session management,
     * JWT filter integration, and role-based endpoint authorization.
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints — public
                .requestMatchers("/api/auth/**").permitAll()
                // Courses — public reads, authenticated writes
                .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyRole("TEACHER", "ADMIN")
                // Lessons — public reads, authenticated writes
                .requestMatchers(HttpMethod.GET, "/api/lessons/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/lessons/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/lessons/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/lessons/**").hasAnyRole("TEACHER", "ADMIN")
                // Quizzes — public reads, authenticated writes
                .requestMatchers(HttpMethod.GET, "/api/quizzes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/quizzes/**").hasAnyRole("TEACHER", "ADMIN")
                // Teachers admin/pending — admin only
                .requestMatchers("/api/teachers/admin").hasRole("ADMIN")
                .requestMatchers("/api/teachers/pending").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/teachers/*/role").hasRole("ADMIN")
                // Teacher preview — public
                .requestMatchers(HttpMethod.GET, "/api/teachers/*/preview").permitAll()
                // Enrollments — authenticated
                .requestMatchers("/api/enrollments/**").authenticated()
                // Everything else — authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean from the given configuration.
     *
     * @param config the Spring authentication configuration
     * @return the authentication manager
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
