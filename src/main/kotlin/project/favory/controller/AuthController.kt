package project.favory.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import project.favory.config.swagger.SecurityNotRequired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import project.favory.dto.auth.request.EmailVerificationConfirmRequest
import project.favory.dto.auth.request.EmailVerificationSendRequest
import project.favory.dto.auth.request.LoginRequest
import project.favory.dto.auth.request.OAuthLoginRequest
import project.favory.dto.auth.request.RefreshTokenRequest
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.auth.response.EmailVerificationConfirmResponse
import project.favory.dto.auth.response.UserResponse
import project.favory.dto.auth.response.LoginResponse
import project.favory.entity.AuthProvider
import project.favory.service.AuthService

@Tag(name = "Auth", description = "유저 회원가입/로그인/로그아웃/이메일 인증")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @SecurityNotRequired
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody req: SignupRequest): ResponseEntity<UserResponse> {
        val result = authService.signup(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @SecurityNotRequired
    @Operation(summary = "이메일 인증번호 발송")
    @PostMapping("/auth/email/send")
    fun sendEmailVerification(@Valid @RequestBody req: EmailVerificationSendRequest): ResponseEntity<Void> {
        authService.sendEmailVerification(req)
        return ResponseEntity.noContent().build()
    }

    @SecurityNotRequired
    @Operation(summary = "이메일 인증번호 확인")
    @PostMapping("/auth/email/verify")
    fun verifyEmail(@Valid @RequestBody req: EmailVerificationConfirmRequest): ResponseEntity<EmailVerificationConfirmResponse> {
        val result = authService.verifyEmail(req)
        return ResponseEntity.ok(result)
    }

    @SecurityNotRequired
    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<LoginResponse> {
        val token = authService.login(req)
        return ResponseEntity.ok(token)
    }

    @SecurityNotRequired
    @Operation(summary = "리프레시 토큰")
    @PostMapping("/refresh-token")
    fun refresh(@Valid @RequestBody req: RefreshTokenRequest): ResponseEntity<LoginResponse> {
        val result = authService.refreshToken(req.refreshToken)
        return ResponseEntity.ok(result)
    }

    @SecurityNotRequired
    @Operation(summary = "OAuth 간편 로그인")
    @PostMapping("/login/{provider}")
    fun oauthLogin(
        @PathVariable provider: AuthProvider,
        @RequestBody req: OAuthLoginRequest
    ): ResponseEntity<LoginResponse> {
        val result = authService.oauthLogin(provider, req.token)
        return ResponseEntity.ok(result)
    }

}
