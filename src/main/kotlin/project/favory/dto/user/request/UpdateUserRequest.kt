package project.favory.dto.user.request

import jakarta.validation.constraints.Size

class UpdateUserRequest {
    @field:Size(max = 50)
    val nickname: String? = null

    @field:Size(max = 500)
    val profileImageUrl: String? = null

    val profileMessage: String? = null
}