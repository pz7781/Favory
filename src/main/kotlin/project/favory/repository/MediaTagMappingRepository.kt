package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.MediaTagMapping

interface MediaTagMappingRepository : JpaRepository<MediaTagMapping, Long>
