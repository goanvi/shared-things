package se.itmo.ru.authservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.services.UserService
import se.itmo.ru.common.dto.request.users.CreateUserRequestDto
import se.itmo.ru.common.dto.response.users.CreateUserResponseDto


@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('SUPER_VAISER')")
class UserController(
        @Autowired
        private val userService: UserService
) {


    @GetMapping
    fun findAll(pageable: Pageable) {
//            val res = this.userService.findAll(pageable)

//            return ResponseEntity.ok().body(res)
    }

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: String) {
    }

    @PostMapping
    fun create(
            @RequestBody dto: CreateUserRequestDto
    ): Mono<ResponseEntity<CreateUserResponseDto>> {
        val res = this.userService.create(dto)
        return res.map { ResponseEntity.ok().body(it) }
    }

    @PatchMapping("/{id}")
    fun update() {

    }

    @DeleteMapping("/{id}")
    fun delete() {
    }
}
