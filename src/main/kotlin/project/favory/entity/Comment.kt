package project.favory.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favory_id", nullable = false)
    val favory: Favory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 100, nullable = false)
    var content: String,

    @Column(nullable = true)
    var deletedAt: LocalDateTime? = null
) : AbstractTimeEntity()
