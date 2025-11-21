package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.Favory

interface FavoryRepository : JpaRepository<Favory, Long>
