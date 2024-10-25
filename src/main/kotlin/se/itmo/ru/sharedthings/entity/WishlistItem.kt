package se.itmo.ru.sharedthings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.sharedthings.enums.WishlistStatus

@Entity
@Table(name = "wishlist_item")
data class WishlistItem(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wishlist_item_wishlist_id_seq")
    @SequenceGenerator(name = "wishlist_item_wishlist_id_seq", allocationSize = 1)
    @Column(name = "wishlist_id")
    val wishlistId: Int? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wishlist_owner", nullable = false)
    val owner: Account,

    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "title", nullable = false, length = 300)
    val title: String,

    @Column(name = "description")
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "found_item", nullable = false)
    val foundItem: Item? = null,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: WishlistStatus,

    @Column(name = "moderated", nullable = false)
    val moderated: Boolean = false,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "wishlist_suggestions",
        joinColumns = [JoinColumn(name = "wishlist_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    val suggestions: MutableList<Item> = mutableListOf(),
)
