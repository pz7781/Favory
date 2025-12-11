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
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        // 중복체크
        if (userRepository.existsByEmail(req.email)) {
            throw IllegalArgumentException("email:이미 사용 중인 이메일입니다.")
        }
        if (userRepository.existsByNickname(req.nickname)) {
            throw IllegalArgumentException("nickname:이미 사용 중인 닉네임입니다.")
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
            ?: throw IllegalArgumentException("이메일이 올바르지 않습니다.")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw IllegalArgumentException("비밀번호가 올바르지 않습니다.")
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
            ?: throw IllegalArgumentException("User not authenticated")
    }

    fun validateUser(ownerId: Long) {
        val currentUserId = getCurrentUserId()
        if (currentUserId != ownerId) {
            throw IllegalArgumentException("Don't have enough permission")
        }
    }

    // 토큰 갱신
    @Transactional(readOnly = true)
    fun refreshToken(refreshToken: String): LoginResponse {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.")
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw IllegalArgumentException("리프레시 토큰이 아닙니다.")
        }

        val userId = jwtTokenProvider.getUserId(refreshToken)
        val emailFromToken = jwtTokenProvider.getEmail(refreshToken)

        val user = userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("사용자를 찾을 수 없습니다.")
        }

        if (user.email != emailFromToken) {
            throw IllegalArgumentException("토큰 정보가 올바르지 않습니다.")
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