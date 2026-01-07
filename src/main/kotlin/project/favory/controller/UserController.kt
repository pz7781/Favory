package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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
    fun getMe(): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getMe())

    @Operation(summary = "id 기반 프로필 조회")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getById(id))

    @Operation(summary = "닉네임 기반 프로필 조회")
    @GetMapping("/profile/{nickname}")
    fun getByNickname(@PathVariable nickname: String): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getByNickname(nickname))

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
}