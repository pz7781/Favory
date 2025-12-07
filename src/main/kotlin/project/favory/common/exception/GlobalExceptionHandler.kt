package project.favory.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import project.favory.dto.common.ErrorResponse
import project.favory.dto.common.FieldErrorDetail
import java.nio.file.attribute.UserPrincipalNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {

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

        val body = ErrorResponse(
            message = firstMessage,
            details = details
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    // 비즈니스 로직에서의 IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException
    ): ResponseEntity<ErrorResponse> {

        val body = ErrorResponse(
            message = ex.message ?: "잘못된 요청입니다.",
            details = null
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    // 유저 없음
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(
        ex: UserNotFoundException
    ): ResponseEntity<ErrorResponse> {

        val body = ErrorResponse(
            message = ex.message ?: "사용자를 찾을 수 없습니다.",
            details = null
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }
}