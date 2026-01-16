package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import project.favory.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByFavoryId(favoryId: Long): List<Comment>

    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.favory f
        JOIN FETCH f.media m
        JOIN FETCH c.user u
        WHERE c.favory.id = :favoryId
        AND c.deletedAt IS NULL
    """)
    fun findAllByFavoryIdWithMedia(favoryId: Long, pageable: Pageable): Page<Comment>

    @Query("""
        SELECT c FROM Comment c
        JOIN FETCH c.favory f
        JOIN FETCH f.media m
        JOIN FETCH c.user u
        WHERE c.user.id = :userId
        AND c.deletedAt IS NULL
    """)
    fun findByUserIdWithMedia(userId: Long, pageable: Pageable): Page<Comment>

    fun findAllByFavoryIdAndDeletedAtIsNull(favoryId: Long, pageable: Pageable): Page<Comment>
    fun findByUserIdAndDeletedAtIsNull(userId: Long, pageable: Pageable): Page<Comment>
}
