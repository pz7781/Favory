package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Review

interface ReviewRepository : JpaRepository<Review, Long>
