package project.favory.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "review")
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    val media: Media,

    @Column(length = 100, nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = true)
    var deletedAt: LocalDateTime?
) : AbstractTimeEntity()
