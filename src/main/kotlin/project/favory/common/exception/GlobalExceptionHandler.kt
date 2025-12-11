package project.favory.common.exception

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

        val raw = ex.message ?: ""
        val parts = raw.split(":", limit = 2)

        val field = if(parts.size == 2) parts[0] else null
        val message = if(parts.size == 2) parts[1] else raw

        val body = ErrorResponse(
            message = message,
            details = null,
            field = field
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }
}