package org.concord.backend.annotation;

import org.concord.backend.annotation.PublicEndpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;


@Component
public class PublicRequestMatcher implements RequestMatcher {

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public PublicRequestMatcher(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        try {
            var handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain == null || !(handlerExecutionChain.getHandler() instanceof HandlerMethod)) {
                return false;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();

            PublicEndpoint annotation = handlerMethod.getMethodAnnotation(PublicEndpoint.class);
            if (annotation == null) {
                annotation = handlerMethod.getBeanType().getAnnotation(PublicEndpoint.class);
            }

            if (annotation != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}