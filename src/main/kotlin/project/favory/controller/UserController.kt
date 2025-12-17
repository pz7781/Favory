package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import project.favory.config.swagger.SecurityNotRequired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import project.favory.dto.user.request.UpdateUserRequest
import project.favory.dto.user.response.UserResponse
import project.favory.service.UserService

@Tag(name = "User", description = "유저 프로필 조회/수정/삭제")
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getMe(): ResponseEntity<UserResponse> {
        val email = getCurrentUserEmail()
        val response = userService.getByEmail(email)
        return ResponseEntity.ok(response)
    }

    @SecurityNotRequired
    @Operation(summary = "유저 조회")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getById(id))

    @Operation(summary = "프로필 수정 (닉네임, 메시지)")
    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.details")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UpdateUserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.update(id, req))

    @Operation(summary = "프로필 이미지 업로드")
    @PutMapping("/{id}/profile-image")
    @PreAuthorize("#id == authentication.details")
    fun uploadProfileImage(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val imageUrl = userService.uploadProfileImage(id, file)
        return ResponseEntity.ok(mapOf("profileImageUrl" to imageUrl))
    }

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.details")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun getCurrentUserEmail(): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "인증 정보가 없습니다."
            )

        val email = authentication.name
        if (email.isNullOrBlank()) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "인증 정보가 올바르지 않습니다."
            )
        }

        return email
    }
}