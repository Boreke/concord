package org.concord.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements Filter {

    @Value("${cors.allowed-origin:*}")
    private String[] allowedOrigin;

    @Value("${cors.postman-testing:false}")
    private boolean postmanTesting;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        boolean isValidOrigin = origin != null && Arrays.stream(allowedOrigin)
                .anyMatch(allowed -> allowed.equals("*") || allowed.equalsIgnoreCase(origin));

        if (origin == null && postmanTesting) {
            isValidOrigin = true;
        }

        if (isValidOrigin) {
            response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            response.setHeader("Access-Control-Expose-Headers", "x-refresh-token, authorization");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Origin: " + origin);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}
    @Override
    public void destroy() {}
}

