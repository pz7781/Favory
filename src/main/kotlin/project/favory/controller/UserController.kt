package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.user.response.UserResponse
import project.favory.service.user.UserService

@Profile("local")
@Tag(name = "User", description = "유저 테스트 CRUD")
@RestController
@RequestMapping("/test/users")
class TestUserController(
    private val userService: UserService
) {
    @Operation(summary = "유저 생성")
    @PostMapping
    fun create(@Valid @RequestBody req: SignupRequest): UserResponse =
        userService.create(req)

    @Operation(summary = "개인 유저 조회")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserResponse =
        userService.getById(id)

    @Operation(summary = "전체 유저 조회")
    @GetMapping
    fun getAll(): List<UserResponse> =
        userService.getAll()

    @Operation(summary = "유저 삭제")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) =
        userService.delete(id)
}