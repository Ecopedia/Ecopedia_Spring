package com.ecopedia.server.global.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public static final String MEMBER_ATTR = "authenticatedMember";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();

        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-resources")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        try {
            String nickname = jwtUtil.validateTokenAndGetNickName(authHeader.substring(7));
            Member member = memberRepository.findByNickname(nickname)
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

            request.setAttribute(MEMBER_ATTR, member);
            return true;

        } catch (TokenExpiredException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return false;
        } catch (JWTVerificationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return false;
        }
    }
}
