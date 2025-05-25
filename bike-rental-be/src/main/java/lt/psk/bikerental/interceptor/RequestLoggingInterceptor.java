package lt.psk.bikerental.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Value("${lt.psk.bikerental.interceptor.RequestLoggingInterceptor.pathPatterns}")
    public List<String> pathPatterns;

    private static final String HEADER_KEY = "Authorization";

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = null;
        if (authentication != null) {
            String auth0id = ((Jwt)authentication.getPrincipal()).getClaim("sub");
            user = userRepository.findByAuth0Id(auth0id).orElse(null);
        } else {
            user = new User();
            user.setFullName("<anonymous>");
            user.setId(new UUID(0, 0));
        }

        var queryString = request.getQueryString();
        queryString = queryString != null ? "?" + queryString : "";

        log.info("%s (%s): %s to %s%s"
                .formatted(user.getFullName(),
                        user.getId().toString(),
                        request.getMethod(),
                        request.getRequestURI(),
                        queryString));
        return true;
    }
}