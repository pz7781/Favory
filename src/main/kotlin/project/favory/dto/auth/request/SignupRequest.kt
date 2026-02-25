package project.favory.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class SignupRequest (
    @field:Email @field:NotBlank
    val email: String,

    @field:NotBlank
    val verifyToken: String,

    @field:NotBlank @field:Size(min = 8, max = 100)
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+]).{8,100}$",
        message = "비밀번호는 영문, 숫자, 특수 문자를 모두 포함해야 합니다."
    )
    val password: String,

    @field:NotBlank
    val passwordConfirmation: String,

    @field:NotBlank @field:Size(min = 3, max = 10)
    @field:Pattern(
        regexp = "^[a-z0-9]{3,10}$",
        message = "닉네임은 소문자 영문 또는 숫자만 사용할 수 있습니다. ( 3~10자 사이 )"
    )
    val nickname: String
){

}
