package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Media

interface MediaRepository : JpaRepository<Media, Long> {
    fun findByExternalId(externalId: String): Media?
}
