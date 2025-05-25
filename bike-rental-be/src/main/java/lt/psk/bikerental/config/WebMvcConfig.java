package lt.psk.bikerental.config;

import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.interceptor.RequestLoggingInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(requestLoggingInterceptor.pathPatterns != null && requestLoggingInterceptor.pathPatterns.size() > 0) {
            registry.addInterceptor(requestLoggingInterceptor)
                    .addPathPatterns(requestLoggingInterceptor.pathPatterns);
        }
    }
}
