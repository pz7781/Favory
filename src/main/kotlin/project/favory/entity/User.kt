package project.favory.entity

import jakarta.persistence.*

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

    @Column(length = 50, nullable = false)
    var nickname: String,

    @Column(length = 500, nullable = true)
    var profileImageUrl: String?,

    @Column(columnDefinition = "TEXT", nullable = true)
    var profileMessage: String?
) : AbstractTimeEntity()
