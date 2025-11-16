package project.favory.entity

import jakarta.persistence.*

@Entity
@Table(name = "media")
class Media(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    val type: MediaType,

    @Column(length = 200, nullable = false)
    var title: String,

    @Column(nullable = false)
    var year: Int,

    @Column(length = 500, nullable = true)
    var imageUrl: String?,

    @Column(length = 200, nullable = false, unique = true)
    val externalId: String
) : AbstractTimeEntity()

enum class MediaType {
    MUSIC,
    MOVIE,
    DRAMA,
    BOOK,
}
