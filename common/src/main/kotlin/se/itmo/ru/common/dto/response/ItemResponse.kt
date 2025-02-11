package se.itmo.ru.common.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import se.itmo.ru.common.ItemStatus
import java.util.*

data class ItemResponse(

    @field:JsonProperty("item_id")
    val itemId: UUID,

    val name: String,

    val description: String?,

    //Account
    val owner: UUID,

    val status: ItemStatus,

    val moderated: Boolean
)
