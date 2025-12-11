package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.FavoryTagMapping

interface FavoryTagMappingRepository : JpaRepository<FavoryTagMapping, Long> {
    fun findAllByFavoryId(favoryId: Long): List<FavoryTagMapping>

    fun findByTagNameContainingIgnoreCase(tag: String, pageable: Pageable): Page<FavoryTagMapping>
}
