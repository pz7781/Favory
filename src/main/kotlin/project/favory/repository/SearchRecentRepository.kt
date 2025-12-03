package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.SearchRecent

interface SearchRecentRepository : JpaRepository<SearchRecent, Long> {
    fun findByUserIdAndKeyword(userId: Long, keyword: String): SearchRecent?
    fun findTop5ByUserIdOrderByLastUsedAtDesc(userId: Long): List<SearchRecent>
    fun findByUserIdOrderByLastUsedAtDesc(userId: Long): List<SearchRecent>
    fun deleteAllByUserId(userId: Long)
}
