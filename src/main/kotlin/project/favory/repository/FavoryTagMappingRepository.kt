package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import project.favory.entity.FavoryTagMapping
import project.favory.entity.MediaType

interface FavoryTagMappingRepository : JpaRepository<FavoryTagMapping, Long> {
    fun findAllByFavoryId(favoryId: Long): List<FavoryTagMapping>

    @Query(
        """
        select ftm
        from FavoryTagMapping ftm
        join fetch ftm.tag t
        join ftm.favory f
        where f.id in :favoryIds
          and f.deletedAt is null
        """
    )
    fun findAllWithTagByFavoryIdIn(
        @Param("favoryIds") favoryIds: List<Long>
    ): List<FavoryTagMapping>

    @Query(
        """
        select ftm
        from FavoryTagMapping ftm
        join ftm.tag t
        join ftm.favory f
        where f.deletedAt is null
          and lower(t.name) like lower(concat(:tag, '%'))
        """
    )
    fun findByTagNameContainingAndFavoryNotDeleted(
        @Param("tag") tag: String,
        pageable: Pageable
    ): Page<FavoryTagMapping>

    @Query(
        """
        select distinct f.id
        from FavoryTagMapping ftm
        join ftm.tag t
        join ftm.favory f
        where f.deletedAt is null
          and lower(t.name) like lower(concat(:tag, '%'))
        """
    )
    fun findFavoryIdsByTagPrefix(
        @Param("tag") tag: String,
        pageable: Pageable
    ): Page<Long>

    @Query(
        """
        select distinct f.id
        from FavoryTagMapping ftm
        join ftm.tag t
        join ftm.favory f
        join f.media m
        where f.deletedAt is null
          and m.type = :mediaType
          and lower(t.name) like lower(concat(:tag, '%'))
        """
    )
    fun findFavoryIdsByTagPrefixAndMediaType(
        @Param("tag") tag: String,
        @Param("mediaType") mediaType: MediaType,
        pageable: Pageable
    ): Page<Long>
}
