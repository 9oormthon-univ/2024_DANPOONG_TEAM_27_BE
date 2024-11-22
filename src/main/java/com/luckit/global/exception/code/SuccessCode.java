package com.luckit.global.exception.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    // 200 OK
    USER_CREATE_SUCCESS(HttpStatus.OK, "사용자 정보 생성을 성공했습니다."),
    GET_TOKEN_SUCCESS(HttpStatus.OK, "Access 토큰을 성공적으로 가져왔습니다."),
    RENEW_TOKEN_SUCCESS(HttpStatus.OK, "Access 토큰을 성공적으로 재발급했습니다."),
    LOGIN_MEMBER_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    LOGOUT_MEMBER_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    WITHDRAW_MEMBER_SUCCESS(HttpStatus.OK, "회원탈퇴에 성공했습니다."),
    GET_MEMBER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 조회에 성공했습니다."),
    UPDATE_MEMBER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 수정에 성공했습니다."),
    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 인증에 성공했습니다."),
    EMAIL_CHECK_SUCCESS(HttpStatus.OK, "이메일 중복 검사에 성공했습니다."),
    GET_USER_FORTUNE_SUCCESS(HttpStatus.OK, "사주 정보 조회에 성공했습니다"),

    ADD_GOAL_SUCCESS(HttpStatus.OK, "목표 생성을 성공했습니다."),
    GET_GOAL_SUCCESS(HttpStatus.OK, "목표 조회를 성공했습니다."),
    COMPLETE_GOAL_SUCCESS(HttpStatus.OK, "목표 완료를 성공했습니다."),
    DELETE_GOAL_SUCCESS(HttpStatus.OK, "목표 삭제를 성공했습니다."),
    GET_GOAL_MYPAGE_SUCCESS(HttpStatus.OK, "마이페이지 - 목표 목록 조회를 성공했습니다."),

    ADD_TODO_SUCCESS(HttpStatus.OK, "미션 생성을 성공했습니다."),
    GET_TODO_SUCCESS(HttpStatus.OK, "미션 조회를 성공했습니다."),
    COMPLETE_TODO_SUCCESS(HttpStatus.OK, "미션 완료를 성공했습니다."),
    DELETE_TODO_SUCCESS(HttpStatus.OK, "미션 삭제를 성공했습니다."),
    GET_TODO_GRAPH_SUCCESS(HttpStatus.OK, "미션 그래프 조회에 성공했습니다."),

    // 201 Created
    CREATE_MEMBER_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),

    // 204 No Content
    DELETE_DIARY_SUCCESS(HttpStatus.NO_CONTENT, "일기 삭제에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
