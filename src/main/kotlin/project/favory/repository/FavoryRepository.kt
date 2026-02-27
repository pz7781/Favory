package project.favory.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import project.favory.entity.Favory
import project.favory.entity.MediaType

interface FavoryRepository : JpaRepository<Favory, Long> {

    @Query(
        value = """
        select distinct f
        from Favory f
        join fetch f.media m
        join fetch f.user u
        where f.deletedAt is null
          and (:category is null or m.type = :category)
          and (
                lower(m.title) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(m.creator, '')) like lower(concat('%', :keyword, '%'))
             or lower(f.title) like lower(concat('%', :keyword, '%'))
          )
        """,
        countQuery = """
        select count(distinct f.id)
        from Favory f
        join f.media m
        join f.user u
        where f.deletedAt is null
          and (:category is null or m.type = :category)
          and (
                lower(m.title) like lower(concat('%', :keyword, '%'))
             or lower(coalesce(m.creator, '')) like lower(concat('%', :keyword, '%'))
             or lower(f.title) like lower(concat('%', :keyword, '%'))
          )
        """
    )
    fun searchCombined(
        @Param("keyword") keyword: String,
        @Param("category") category: MediaType?,
        pageable: Pageable
    ): Page<Favory>


    @EntityGraph(attributePaths = ["user", "media"])
    fun findByDeletedAtIsNull(pageable: Pageable): Page<Favory>


    @EntityGraph(attributePaths = ["user", "media"])
    fun findByMedia_TypeAndDeletedAtIsNull(
        type: MediaType,
        pageable: Pageable
    ): Page<Favory>


    @EntityGraph(attributePaths = ["user", "media"])
    fun findByUserIdAndDeletedAtIsNull(userId: Long, pageable: Pageable): Page<Favory>


    @EntityGraph(attributePaths = ["user", "media"])
    fun findByUserIdAndMedia_TypeAndDeletedAtIsNull(
        userId: Long,
        type: MediaType,
        pageable: Pageable
    ): Page<Favory>

    @Query(
        """
        select distinct f
        from Favory f
        join FavoryTagMapping ftm on ftm.favory = f
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
        join FavoryTagMapping ftm on ftm.favory = f
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