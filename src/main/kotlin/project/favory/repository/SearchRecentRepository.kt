package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.SearchRecent

interface SearchRecentRepository : JpaRepository<SearchRecent, Long>
