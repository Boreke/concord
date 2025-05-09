package org.concord.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.concord.backend.annotation.Public;
import org.concord.backend.security.JwtAuthEntryPoint;
import org.concord.backend.security.JwtAuthFilter;
import org.concord.backend.service.UserDetailsServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint authEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;
    private final ApplicationContext applicationContext;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, JwtAuthEntryPoint authEntryPoint,
                          UserDetailsServiceImpl userDetailsService, ApplicationContext applicationContext) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authEntryPoint = authEntryPoint;
        this.userDetailsService = userDetailsService;
        this.applicationContext = applicationContext;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestMatcher publicRequestMatcher = request -> {
            try {
                RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
                HandlerMethod handlerMethod = (HandlerMethod) mapping.getHandler(request).getHandler();
                return handlerMethod.hasMethodAnnotation(Public.class);
            } catch (Exception e) {
                return false;
            }
        };

        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(publicRequestMatcher).permitAll();
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    auth.anyRequest().authenticated();
                });

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}