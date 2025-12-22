package project.favory.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import project.favory.dto.common.ErrorResponse
import project.favory.dto.common.FieldErrorDetail

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    // 커스텀 비즈니스 예외 처리
    @ExceptionHandler(FavoryException::class)
    fun handleFavoryException(ex: FavoryException): ResponseEntity<ErrorResponse> {
        log.warn("FavoryException: [{}] {}", ex.errorCode, ex.message)

        val response = ErrorResponse.of(
            errorCode = ex.errorCode,
            message = ex.message,
            field = ex.field
        )

        return ResponseEntity
            .status(ex.errorCode.status)
            .body(response)
    }

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        log.warn("ValidationException: {}", ex.message)

        val fieldErrors = ex.bindingResult.fieldErrors

        val details: Map<String, FieldErrorDetail> =
            fieldErrors
                .groupBy(FieldError::getField)
                .mapValues { (_, errors) ->
                    val msg = errors.first().defaultMessage ?: "유효하지 않은 값입니다."
                    FieldErrorDetail(message = msg)
                }

        val firstMessage = fieldErrors.firstOrNull()?.defaultMessage
            ?: "잘못된 요청입니다."

        val response = ErrorResponse.of(
            errorCode = ErrorCode.INVALID_INPUT,
            message = firstMessage,
            details = details
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    // 기존 예외 처리 (마이그레이션 기간용)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.warn("IllegalArgumentException (legacy): {}", ex.message)

        val response = ErrorResponse.of(
            errorCode = ErrorCode.INVALID_INPUT,
            message = ex.message ?: "잘못된 요청입니다."
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        log.warn("NoSuchElementException (legacy): {}", ex.message)

        val response = ErrorResponse.of(
            errorCode = ErrorCode.USER_NOT_FOUND,
            message = ex.message ?: "리소스를 찾을 수 없습니다."
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    // 500 에러 처리
    @ExceptionHandler(Exception::class)
    fun handleAllException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception: ", ex)

        val response = ErrorResponse.of(ErrorCode.INTERNAL_ERROR)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
}
