package com.ecopedia.server.apiPayload.code.status;

import com.ecopedia.server.apiPayload.code.BaseErrorCode;
import com.ecopedia.server.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 기본 응답
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없는 요청입니다."),
    _LOGIN_FAILURE(HttpStatus.NOT_FOUND, "COMMON404", "요청 리소스를 찾을 수 없습니다."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _SERVICE_UNAVAILABLE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "COMMON503", "서버가 일시적으로 사용중지 되었습니다."),


    // 회원가입, 로그인
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "유저가 존재하지 않습니다."),
    NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "동일한 닉네임이 존재합니다."),
    NICKNAME_LENGTH_TOO_LONG(HttpStatus.BAD_REQUEST, "MEMBER4003", "10글자 이내로 닉네임을 설정해주세요."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, "MEMBER4004", "비밀번호가 틀렸습니다."),

    // 도감
    BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOK4001", "도감이 존재하지 않습니다."),

    // 카테고리
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY4001", "존재하지 않는 카테고리입니다."),

    // 생명관련
    CREATURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "CREATUREY4001", "존재하지 않는 생물입니다."),


    // jwt
    JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT401", "토큰이 존재하지 않습니다."),
    JWT_INVALID_FORMAT(HttpStatus.UNAUTHORIZED, "JWT402", "토큰 형식이 유효하지 않습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT403", "유효하지 않은 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT404", "토큰이 만료되었습니다."),

    // 이미지 관련
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST,"IMG4001", "이미지가 존재하지 않습니다."),
    S3_UPLOAD_FAILED(HttpStatus.BAD_REQUEST,"IMG4002", "이미지 인식에 실패했습니다."),
    AI_ANALYSIS_FAILED(HttpStatus.BAD_REQUEST, "IMG4003", "이미지 분석에 실패했습니다."),

    // 후원 관련 에러
    EXCEEDED_DONATION(HttpStatus.BAD_REQUEST, "DONATION4001", "후원 가능한 나무 개수를 초과했습니다."),
    CAMPAIGN_NOT_FOUND(HttpStatus.BAD_REQUEST, "CAMPAIGN4001", "후원의 대상 캠페인을 찾을 수 없습니다.")
    ;



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
