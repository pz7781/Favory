package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Comment

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByFavoryId(favoryId: Long): List<Comment>
}
