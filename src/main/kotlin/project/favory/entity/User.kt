package project.favory.entity

import jakarta.persistence.*

@Entity
@Table(name = "users",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_users_provider_provider_id",
            columnNames = ["provider", "provider_id"]
        )
    ]
)
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

    @Column(length = 50, nullable = false)
    var nickname: String,

    @Column(length = 500, nullable = true)
    var profileImageUrl: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    var profileMessage: String? = null
) : AbstractTimeEntity()

enum class AuthProvider {
    LOCAL, GOOGLE
}