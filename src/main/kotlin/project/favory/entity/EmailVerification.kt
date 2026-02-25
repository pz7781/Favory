package project.favory.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "email_verifications")
class EmailVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(name = "code_hash", nullable = false, length = 255)
    var codeHash: String,

    @Column(name = "code_expires_at", nullable = false)
    var codeExpiresAt: LocalDateTime,

    @Column(name = "verify_token_hash", length = 255)
    var verifyTokenHash: String? = null,

    @Column(name = "verify_token_expires_at")
    var verifyTokenExpiresAt: LocalDateTime? = null,

    @Column(name = "verified_at")
    var verifiedAt: LocalDateTime? = null
) : AbstractTimeEntity()
