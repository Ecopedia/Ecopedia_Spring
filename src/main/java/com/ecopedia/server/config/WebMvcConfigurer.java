package com.ecopedia.server.config;

import com.ecopedia.server.global.auth.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfigurer  implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/v1/**") // 보호할 경로
                .excludePathPatterns("/login", "/signup"); // 인증 필요 없는 경로
    }
}
