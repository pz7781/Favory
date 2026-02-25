package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.EmailVerification

interface EmailVerificationRepository : JpaRepository<EmailVerification, Long> {
    fun findByEmail(email: String): EmailVerification?
}
