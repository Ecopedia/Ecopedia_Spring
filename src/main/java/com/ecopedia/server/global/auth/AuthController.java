package com.ecopedia.server.global.auth;

import com.ecopedia.server.apiPayload.code.status.ErrorStatus;
import com.ecopedia.server.apiPayload.exception.handler.ErrorHandler;
import com.ecopedia.server.global.auth.authDto.LoginRequest;
import com.ecopedia.server.global.auth.authDto.SignupRequest;
import com.ecopedia.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecopedia.server.domain.Member;

import java.util.Collections;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (memberRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new ErrorHandler(ErrorStatus.NICKNAME_ALREADY_EXIST);
        }
        if (request.getNickname().length() > 10) {
            throw new ErrorHandler(ErrorStatus.NICKNAME_LENGTH_TOO_LONG);
        }

        String hashedPassword = passwordUtil.hashPassword(request.getPassword());

        Member member = Member.builder()
                .nickname(request.getNickname())
                .password(hashedPassword)
                .build();

        memberRepository.save(member);
        return ResponseEntity.ok("회원가입성공");
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Member member = memberRepository.findByNickname(request.getNickname())
                .orElse(null);

        if (member == null) {
            throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        if (!passwordUtil.verifyPassword(request.getPassword(), member.getPassword())) {
            throw new ErrorHandler(ErrorStatus.);
        }

        String token = jwtUtil.generateToken(member.getNickname());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}

