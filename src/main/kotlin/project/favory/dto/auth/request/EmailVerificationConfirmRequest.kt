package project.favory.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class EmailVerificationConfirmRequest(
    @field:Email @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
    val code: String
)
