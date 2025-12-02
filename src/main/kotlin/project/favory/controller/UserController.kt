package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import project.favory.dto.user.request.UpdateUserRequest
import project.favory.dto.user.response.UserResponse
import project.favory.repository.UserRepository
import project.favory.service.UserService

@Tag(name = "User", description = "유저 프로필 조회/수정/삭제")
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    @GetMapping("/me")
    fun getMe(): UserResponse {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.")

        val email = authentication.name
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.")

        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "유저를 찾을 수 없습니다.")

        return UserResponse(
            id = user.id!!,
            email = user.email,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl,
            profileMessage = user.profileMessage
        )
    }

    @Operation(summary = "유저 조회")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getById(id))

    @Operation(summary = "프로필 수정")
    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.details")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UpdateUserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.update(id, req))

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.details")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}