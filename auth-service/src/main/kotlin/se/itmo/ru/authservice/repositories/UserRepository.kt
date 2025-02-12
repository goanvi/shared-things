package se.itmo.ru.authservice.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import se.itmo.ru.authservice.models.User
import java.util.UUID

@Repository
interface UserRepository : ReactiveCrudRepository<User, UUID> {
//    findByName
}