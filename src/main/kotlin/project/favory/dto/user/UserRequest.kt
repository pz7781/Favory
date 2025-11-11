package project.favory.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class UserRequest (
    @field:Email @field:NotBlank
    val email: String,

    @field:NotBlank @field:Size(min = 8, max = 100)
    val password: String,

    @field:NotBlank @field:Size(max = 8)
    val nickname: String,

    @field:Size(max = 500)
    val profileImageUrl: String? = null,

    val profileMessage: String? = null
){

}