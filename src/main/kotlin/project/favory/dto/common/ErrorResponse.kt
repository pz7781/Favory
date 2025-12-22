package project.favory.dto.common

import com.fasterxml.jackson.annotation.JsonInclude
import project.favory.common.exception.ErrorCode
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val errorCode: Int,
    val errorName: String,
    val message: String,
    val field: String? = null,
    val details: Map<String, FieldErrorDetail>? = null
) {
    companion object {
        fun of(errorCode: ErrorCode, field: String? = null): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status.value(),
                errorCode = errorCode.code,
                errorName = errorCode.name,
                message = errorCode.message,
                field = field
            )
        }

        fun of(
            errorCode: ErrorCode,
            message: String,
            field: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status.value(),
                errorCode = errorCode.code,
                errorName = errorCode.name,
                message = message,
                field = field
            )
        }

        fun of(
            errorCode: ErrorCode,
            message: String,
            details: Map<String, FieldErrorDetail>
        ): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status.value(),
                errorCode = errorCode.code,
                errorName = errorCode.name,
                message = message,
                details = details
            )
        }
    }
}
