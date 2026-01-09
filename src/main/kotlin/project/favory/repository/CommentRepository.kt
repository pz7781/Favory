package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByFavoryId(favoryId: Long): List<Comment>
    fun findAllByFavoryIdAndDeletedAtIsNull(favoryId: Long, pageable: Pageable): Page<Comment>
    fun findByUserIdAndDeletedAtIsNull(userId: Long, pageable: Pageable): Page<Comment>
}
