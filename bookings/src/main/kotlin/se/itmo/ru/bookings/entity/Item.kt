package se.itmo.ru.bookings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.bookings.enum.ItemStatus

@Entity
@Table(name = "item")
data class Item(

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_item_id_seq")
    @SequenceGenerator(name = "item_item_id_seq", allocationSize = 1)
    @Column(name = "item_id", nullable = false)
    val itemId: Int = 0,

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
    var status: ItemStatus,

    @NotNull
    @Column(name = "moderated", nullable = false)
    var moderated: Boolean = false

)
