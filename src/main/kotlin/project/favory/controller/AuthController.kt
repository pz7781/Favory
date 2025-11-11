package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.auth.request.LoginRequest
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.auth.response.TokenResponse
import project.favory.dto.user.response.UserResponse
import project.favory.service.auth.AuthService

@Tag(name = "Auth", description = "유저 회원가입/로그인/로그아웃")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody req: SignupRequest): ResponseEntity<UserResponse> {
        val result = authService.signup(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<TokenResponse> {
        val token = authService.login(req)
        return ResponseEntity.ok(token)
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    fun logout(): ResponseEntity<String> {
        // token 삭제 필요
        return ResponseEntity.ok("로그아웃 성공")
    }

}