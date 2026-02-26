package project.favory.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favory_id", nullable = false)
    val favory: Favory,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)