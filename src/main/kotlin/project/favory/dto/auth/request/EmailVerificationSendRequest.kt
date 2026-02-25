package project.favory.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class EmailVerificationSendRequest(
    @field:Email @field:NotBlank
    val email: String
)
