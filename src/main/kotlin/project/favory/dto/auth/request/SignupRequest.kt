package project.favory.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class SignupRequest (
    @field:Email @field:NotBlank
    val email: String,

    @field:NotBlank @field:Size(min = 8, max = 100)
    val password: String,

    @field:NotBlank @field:Size(max = 50)
    val nickname: String,

    @field:Size(max = 500)
    val profileImageUrl: String? = null,

    val profileMessage: String? = null
){

}