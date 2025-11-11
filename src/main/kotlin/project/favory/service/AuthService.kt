package project.favory.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.dto.auth.request.LoginRequest
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.auth.response.TokenResponse
import project.favory.dto.user.response.UserResponse
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

        // 중복체크
        if (userRepository.existsByEmail(req.email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다.")
        }
        if (userRepository.existsByNickname(req.nickname)) {
            throw IllegalArgumentException("이미 사용 중인 닉네임입니다.")
        }


        // 비밀번호 암호화
        val encoded = passwordEncoder.encode(req.password)

        // 저장
        val saved = userRepository.save(
            User(
                email = req.email,
                password = encoded,
                nickname = req.nickname,
                profileImageUrl = req.profileImageUrl,
                profileMessage = req.profileMessage
            )
        )
        return saved.toResponse()
    }

    // 로그인
    @Transactional(readOnly = true)
    fun login(req: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw IllegalArgumentException("이메일이 올바르지 않습니다.")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw IllegalArgumentException("비밀번호가 올바르지 않습니다.")
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, user.email)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, user.email)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun User.toResponse() = UserResponse(
        id = this.id!!,
        email = this.email,
        nickname = this.nickname,
        profileImageUrl = this.profileImageUrl,
        profileMessage = this.profileMessage
    )

}