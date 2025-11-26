package project.favory.entity

import jakarta.persistence.*

@Entity
@Table(name = "favory_tag_mappings")
class FavoryTagMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favory_id", nullable = false)
    val favory: Favory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag
) : AbstractTimeEntity()
