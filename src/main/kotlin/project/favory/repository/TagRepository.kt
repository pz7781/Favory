package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Tag

interface TagRepository : JpaRepository<Tag, Long>
