package project.favory.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "search_recent")
class SearchRecent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(length = 200, nullable = false)
    var keyword: String,

    @Column(nullable = false)
    var lastUsedAt: LocalDateTime = LocalDateTime.now()
) : AbstractTimeEntity()
