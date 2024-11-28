package se.itmo.ru.sharedthings.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.service.ItemService

@RestController
@RequestMapping("api/item")
class ItemController(
    private val service: ItemService
) {

    @PostMapping("/create")
    fun createItem(
        @RequestParam("ownerId") ownerId: Int,
        @Valid @RequestBody itemDto: ItemDto
    ): ItemDto =
        service.createItem(ownerId, itemDto)

    @GetMapping("/account")
    fun getModeratedAccountItems(
        @RequestParam("accountId") accountId: Int,
        pageable: Pageable
    ): Page<ItemDto> =
        service.getAllModeratedAccountItems(accountId, pageable)

    @PutMapping("/{id}")
    fun updateAccountItem(
        @PathVariable("id") accountId: Int,
        @Valid @RequestBody itemDto: ItemDto
    ): Unit =
        service.updateAccountItem(accountId, itemDto)
}