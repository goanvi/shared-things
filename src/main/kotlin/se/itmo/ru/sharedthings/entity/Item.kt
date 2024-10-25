package se.itmo.ru.sharedthings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.sharedthings.enums.ItemStatus

@Entity
@Table(name = "item")
data class Item(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_item_id_seq")
    @SequenceGenerator(name = "item_item_id_seq", allocationSize = 1)
    @Column(name = "item_id", nullable = false)
    val itemId: Int? = null,

    @Size(min = 1, max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "description")
    val description: String? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: Account,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: ItemStatus,

    @NotNull
    @Column(name = "moderated", nullable = false)
    val moderated: Boolean = false

)
