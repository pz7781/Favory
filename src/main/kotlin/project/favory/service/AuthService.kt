package project.favory.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.auth.request.LoginRequest
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.auth.response.LoginResponse
import project.favory.dto.auth.response.UserResponse
import project.favory.entity.User
import project.favory.repository.UserRepository
import project.favory.security.JwtTokenProvider
import project.favory.common.exception.*

// 회원가입, 로그인, JWT 발급

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // 회원가입
    @Transactional
    fun signup(req: SignupRequest): UserResponse {

        // 비밀번호 확인 일치 체크
        if (req.password != req.passwordConfirmation) {
            throw BadRequestException(ErrorCode.PASSWORD_MISMATCH)
        }

        // 중복체크
        if (userRepository.existsByEmail(req.email)) {
            throw BadRequestException(ErrorCode.DUPLICATE_EMAIL, field = "email")
        }
        if (userRepository.existsByNickname(req.nickname)) {
            throw BadRequestException(ErrorCode.DUPLICATE_NICKNAME, field = "nickname")
        }

        // 비밀번호 암호화
        val encoded = passwordEncoder.encode(req.password)

        // 저장
        val saved = userRepository.save(
            User(
                email = req.email,
                password = encoded,
                nickname = req.nickname
            )
        )
        return saved.toAuthResponse()
    }

    // 로그인
    @Transactional(readOnly = true)
    fun login(req: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw UnauthorizedException(ErrorCode.INVALID_EMAIL)

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw UnauthorizedException(ErrorCode.INVALID_PASSWORD)
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, user.email)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, user.email)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            user = user.toAuthResponse()
        )
    }

    fun getCurrentUserId(): Long {
        return SecurityContextHolder.getContext().authentication?.details as? Long
            ?: throw UnauthorizedException(ErrorCode.NOT_AUTHENTICATED)
    }

    fun validateUser(ownerId: Long) {
        val currentUserId = getCurrentUserId()
        if (currentUserId != ownerId) {
            throw ForbiddenException(ErrorCode.ACCESS_DENIED)
        }
    }

    // 토큰 갱신
    @Transactional(readOnly = true)
    fun refreshToken(refreshToken: String): LoginResponse {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw UnauthorizedException(ErrorCode.NOT_REFRESH_TOKEN)
        }

        val userId = jwtTokenProvider.getUserId(refreshToken)
        val emailFromToken = jwtTokenProvider.getEmail(refreshToken)

        val user = userRepository.findById(userId).orElseThrow {
            NotFoundException(ErrorCode.USER_NOT_FOUND)
        }

        if (user.email != emailFromToken) {
            throw UnauthorizedException(ErrorCode.INVALID_TOKEN_INFO)
        }

        val newAccessToken = jwtTokenProvider.generateAccessToken(user.id!!, user.email)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, user.email)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            tokenType = "Bearer",
            user = user.toAuthResponse()
        )
    }

    private fun User.toAuthResponse() = UserResponse(
        id = this.id!!,
        email = this.email,
        nickname = this.nickname,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

}