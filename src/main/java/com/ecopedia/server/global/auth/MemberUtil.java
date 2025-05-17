package com.ecopedia.server.global.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.domain.Member;
import com.ecopedia.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public Member getMemberFromToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new ErrorHandler(ErrorStatus.JWT_NOT_FOUND);
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new ErrorHandler(ErrorStatus.JWT_INVALID_FORMAT);
        }

        String token = authHeader.substring(7);
        String nickname;

        try {
            nickname = jwtUtil.validateTokenAndGetNickName(token);
        } catch (TokenExpiredException e) {
            throw new ErrorHandler(ErrorStatus.JWT_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new ErrorHandler(ErrorStatus.JWT_INVALID);
        }

        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }
}
