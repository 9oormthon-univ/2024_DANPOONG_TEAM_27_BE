package com.luckit.global.exception.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // 400 Bad Request
    PASSWORD_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다."),
    INVALID_SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "JWT 토큰의 서명이 올바르지 않습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 Enum 타입 값이 있습니다."),
    JSON_SYNTAX_ERROR(HttpStatus.BAD_REQUEST, "JSON 파싱 오류 발생"),
    JSON_SERIALIZATION_ERROR(HttpStatus.BAD_REQUEST, "JSON 직렬화 오류 발생"),
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "유효성 검사에 맞지않습니다."),

    NO_USER_INFO(HttpStatus.BAD_REQUEST, "사용자 정보가 존재하지 않습니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),


    // 401 Unauthorized
    INVALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 403 Forbidden
    FORBIDDEN_ACCESS_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    FORBIDDEN_AUTH_EXCEPTION(HttpStatus.FORBIDDEN, "권한 정보가 없는 토큰입니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),

    // 404 NOT FOUND
    NOT_FOUND_ID_EXCEPTION(HttpStatus.NOT_FOUND, "해당 ID를 찾을 수 없습니다."),
    NOT_FOUND_EMAIL_EXCEPTION(HttpStatus.NOT_FOUND, "해당 이메일의 사용자를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // 409 Conflict
    ALREADY_EXIST_MEMBER_EXCEPTION(HttpStatus.CONFLICT, "이미 회원가입이 완료된 사용자입니다."),

    // 500 Internal Server Exception
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),
    TOKEN_CREATION_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성 중 오류가 발생했습니다."),

    // 503 Service Unavailable
    FAILED_GET_TOKEN_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE, "토큰을 가져오는 중 오류가 발생했습니다."),
    FAILED_LOGOUT_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE, "로그아웃 중 오류가 발생했습니다."),
    FAILED_WITHDRAW_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE, "회원탈퇴 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

