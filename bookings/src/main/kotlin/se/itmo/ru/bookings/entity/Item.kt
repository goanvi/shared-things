package se.itmo.ru.bookings.entity

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import se.itmo.ru.bookings.enum.ItemStatus
import java.util.*

@Table(name = "item")
data class Item(

    @field:Id
    @field:NotNull
    @field:Column("item_id")
    val itemId: UUID,

    @field:Size(min = 1, max = 100)
    @field:NotNull
    val name: String,

    val description: String?,

    //Account
    @field:Column("owner_id")
    @field:NotNull
    val owner: UUID,

    @field:NotNull
    var status: ItemStatus,

    @field:NotNull
    var moderated: Boolean = false

)
