package project.favory.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.common.exception.*
import project.favory.dto.auth.request.EmailVerificationConfirmRequest
import project.favory.dto.auth.request.EmailVerificationSendRequest
import project.favory.dto.auth.request.LoginRequest
import project.favory.dto.auth.request.SignupRequest
import project.favory.dto.auth.response.EmailVerificationConfirmResponse
import project.favory.dto.auth.response.LoginResponse
import project.favory.dto.auth.response.UserResponse
import project.favory.entity.AuthProvider
import project.favory.entity.User
import project.favory.repository.UserRepository
import project.favory.security.JwtTokenProvider
import project.favory.security.oauth.GoogleTokenVerifier
import java.util.UUID

// 회원가입, 로그인, JWT 발급

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val emailVerificationService: EmailVerificationService
) {

    @Transactional
    fun signup(req: SignupRequest): UserResponse {

        if (req.password != req.passwordConfirmation) {
            throw BadRequestException(ErrorCode.PASSWORD_MISMATCH)
        }

        val nickname = req.nickname.trim().lowercase()

        if (userRepository.existsByEmail(req.email)) {
            throw BadRequestException(ErrorCode.DUPLICATE_EMAIL, field = "email")
        }
        if (userRepository.existsByNickname(nickname)) {
            throw BadRequestException(ErrorCode.DUPLICATE_NICKNAME, field = "nickname")
        }

        emailVerificationService.validateAndConsume(req.email, req.verifyToken)

        val encoded = passwordEncoder.encode(req.password)

        val saved = userRepository.save(
            User(
                email = req.email,
                password = encoded,
                nickname = nickname
            )
        )
        return saved.toAuthResponse()
    }

    @Transactional
    fun sendEmailVerification(req: EmailVerificationSendRequest) {
        if (userRepository.existsByEmail(req.email)) {
            throw BadRequestException(ErrorCode.DUPLICATE_EMAIL, field = "email")
        }

        emailVerificationService.sendCode(req.email)
    }

    @Transactional
    fun verifyEmail(req: EmailVerificationConfirmRequest): EmailVerificationConfirmResponse {
        val verifyToken = emailVerificationService.verifyCode(req.email, req.code)
        return EmailVerificationConfirmResponse(verifyToken = verifyToken)
    }

    @Transactional(readOnly = true)
    fun login(req: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw UnauthorizedException(ErrorCode.INVALID_EMAIL)

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw UnauthorizedException(ErrorCode.INVALID_PASSWORD)
        }

        return issueTokens(user)

    }

    @Transactional
    fun oauthLogin(provider: AuthProvider, token: String): LoginResponse {
        return when (provider) {
            AuthProvider.GOOGLE -> googleLogin(token)
            AuthProvider.LOCAL -> throw BadRequestException(ErrorCode.INVALID_INPUT)
        }
    }

    @Transactional
    fun googleLogin(idToken: String): LoginResponse {
        val info = runCatching { googleTokenVerifier.verify(idToken) }
            .getOrElse {
                throw UnauthorizedException(ErrorCode.INVALID_OAUTH_TOKEN)
            }

        val userByProvider = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, info.sub)
        val user = when {
            userByProvider != null -> userByProvider

            userRepository.existsByEmail(info.email) -> {
                throw BadRequestException(ErrorCode.OAUTH_EMAIL_ALREADY_EXISTS)
            }

            else -> {
                val nickname = generateUniqueNickname(info.email)
                userRepository.save(
                    User(
                        email = info.email,
                        password = passwordEncoder.encode(UUID.randomUUID().toString()),
                        provider = AuthProvider.GOOGLE,
                        providerId = info.sub,
                        nickname = nickname,
                        profileImageUrl = info.picture
                    )
                )
            }
        }

        return issueTokens(user)

    }

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

        return issueTokens(user)

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

    private fun issueTokens(user: User): LoginResponse {
        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, user.email)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, user.email)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            user = user.toAuthResponse()
        )
    }

    private val EMAIL_LOCALPART_REGEX = Regex("^[a-z0-9]+$")
    private val NICKNAME_CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789"

    private fun generateUniqueNickname(email: String): String {
        val local = email.substringBefore("@").lowercase()

        val candidateFromEmail: String? =
            if (local.length >= 3 && EMAIL_LOCALPART_REGEX.matches(local)) {
                local.take(10)
            } else null

        if (candidateFromEmail != null && !userRepository.existsByNickname(candidateFromEmail)) {
            return candidateFromEmail
        }

        repeat(20) {
            val randomNick = generateRandomNickname(3, 10)
            if (!userRepository.existsByNickname(randomNick)) return randomNick
        }

        throw InternalServerException(ErrorCode.NICKNAME_GENERATION_FAILED)
    }

    private fun generateRandomNickname(min: Int, max: Int): String {
        val len = kotlin.random.Random.nextInt(min, max + 1)
        return buildString(len) {
            repeat(len) {
                append(NICKNAME_CHARSET[kotlin.random.Random.nextInt(NICKNAME_CHARSET.length)])
            }
        }
    }

    private fun User.toAuthResponse() = UserResponse(
        id = this.id!!,
        email = this.email,
        nickname = this.nickname,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

}
