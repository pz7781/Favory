package project.favory.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.favory.common.exception.BadRequestException
import project.favory.common.exception.ErrorCode
import project.favory.entity.EmailVerification
import project.favory.repository.EmailVerificationRepository
import java.time.LocalDateTime
import java.util.*

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val emailSenderService: EmailSenderService,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${auth.email-verification.code-expiration-minutes}") private val codeExpirationMinutes: Long,
    @Value("\${auth.email-verification.token-expiration-minutes}") private val tokenExpirationMinutes: Long
) {

    @Transactional
    fun sendCode(email: String) {
        val code = generateVerificationCode()
        val now = LocalDateTime.now()

        val verification = emailVerificationRepository.findByEmail(email) ?:
            EmailVerification(
                email = email,
                codeHash = "",
                codeExpiresAt = now
            )

        verification.codeHash = passwordEncoder.encode(code)
        verification.codeExpiresAt = now.plusMinutes(codeExpirationMinutes)
        verification.verifyTokenHash = null
        verification.verifyTokenExpiresAt = null
        verification.verifiedAt = null

        emailVerificationRepository.save(verification)
        emailSenderService.sendVerificationCode(email, code)
    }

    @Transactional
    fun verifyCode(email: String, code: String): String {
        val verification = emailVerificationRepository.findByEmail(email)
            ?: throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE)

        if (verification.codeExpiresAt.isBefore(LocalDateTime.now())) {
            throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE)
        }

        if (!passwordEncoder.matches(code, verification.codeHash)) {
            throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE)
        }

        val verifyToken = UUID.randomUUID().toString().replace("-", "")
        val now = LocalDateTime.now()

        verification.verifyTokenHash = passwordEncoder.encode(verifyToken)
        verification.verifyTokenExpiresAt = now.plusMinutes(tokenExpirationMinutes)
        verification.verifiedAt = now

        emailVerificationRepository.save(verification)

        return verifyToken
    }

    @Transactional
    fun validateAndConsume(email: String, verifyToken: String) {
        val verification = emailVerificationRepository.findByEmail(email)
            ?: throw BadRequestException(ErrorCode.EMAIL_VERIFICATION_REQUIRED)

        val tokenHash = verification.verifyTokenHash
            ?: throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN)
        val tokenExpiresAt = verification.verifyTokenExpiresAt
            ?: throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN)

        if (tokenExpiresAt.isBefore(LocalDateTime.now())) {
            throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN)
        }

        if (!passwordEncoder.matches(verifyToken, tokenHash)) {
            throw BadRequestException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN)
        }

        emailVerificationRepository.delete(verification)
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }
}
