package project.favory.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: Int,
    val message: String
) {
    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 40001, "잘못된 입력입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 40002, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 40003, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, 40004, "이미 사용 중인 닉네임입니다."),
    OAUTH_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 40005, "이미 이 이메일로 가입된 계정이 있습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, 40006, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, 40007, "파일 크기가 제한을 초과했습니다."),

    // 401 Unauthorized
    INVALID_EMAIL(HttpStatus.UNAUTHORIZED, 40101, "이메일이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, 40102, "비밀번호가 올바르지 않습니다."),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, 40103, "인증되지 않은 사용자입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40104, "유효하지 않은 리프레시 토큰입니다."),
    NOT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40105, "리프레시 토큰이 아닙니다."),
    INVALID_TOKEN_INFO(HttpStatus.UNAUTHORIZED, 40106, "토큰 정보가 올바르지 않습니다."),
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, 40107, "유효하지 않은 소셜 로그인 토큰입니다."),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, 40301, "접근 권한이 없습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 40401, "사용자를 찾을 수 없습니다."),
    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, 40402, "미디어를 찾을 수 없습니다."),
    FAVORY_NOT_FOUND(HttpStatus.NOT_FOUND, 40403, "Favory를 찾을 수 없습니다."),
    FAVORY_DELETED(HttpStatus.NOT_FOUND, 40404, "삭제된 Favory입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 40405, "댓글을 찾을 수 없습니다."),
    COMMENT_DELETED(HttpStatus.NOT_FOUND, 40406, "삭제된 댓글입니다."),

    // 500 Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 내부 오류가 발생했습니다.")
}