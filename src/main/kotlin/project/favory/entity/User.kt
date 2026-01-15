package project.favory.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val provider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "provider_id", length = 100, nullable = true)
    val providerId: String? = null,

    @Column(nullable = false, unique = true, length = 10)
    @field:Size(min = 3, max = 10)
    @field:Pattern(regexp = "^[a-z0-9]+$")
    var nickname: String,

    @Column(length = 500, nullable = true)
    var profileImageUrl: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    var profileMessage: String? = null
) : AbstractTimeEntity()

enum class AuthProvider {
    LOCAL, GOOGLE
}