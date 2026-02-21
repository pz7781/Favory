package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import project.favory.entity.Favory
import project.favory.entity.FavoryTagMapping
import project.favory.entity.MediaType

interface FavoryTagMappingRepository : JpaRepository<FavoryTagMapping, Long> {
    fun findAllByFavoryId(favoryId: Long): List<FavoryTagMapping>

    @Query(
        """
        select distinct f
        from Favory f
        join f.tagMappings ftm
        join ftm.tag t
        where f.deletedAt is null
          and lower(t.name) like lower(concat(:tag, '%'))
        """
    )
    fun findByTagNameContainingAndFavoryNotDeleted(
        @Param("tag") tag: String,
        pageable: Pageable
    ): Page<Favory>

    @Query(
        """
        select distinct f
        from Favory f
        join f.tagMappings ftm
        join ftm.tag t
        join f.media m
        where f.deletedAt is null
          and m.type = :mediaType
          and lower(t.name) like lower(concat(:tag, '%'))
        """
    )
    fun findByTagNameAndMediaType(
        @Param("tag") tag: String,
        @Param("mediaType") mediaType: MediaType,
        pageable: Pageable
    ): Page<Favory>
}
