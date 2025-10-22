package project.favory.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.favory.entity.User

interface UserRepository : JpaRepository<User, Long>
