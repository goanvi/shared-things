package se.itmo.ru.bookings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import se.itmo.ru.bookings.enum.BookingStatus
import java.time.LocalDateTime

@Entity
@Table(name = "booking")
data class Booking(

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_booking_id_seq")
    @SequenceGenerator(name = "booking_booking_id_seq", allocationSize = 1)
    @Column(name = "booking_id")
    val bookingId: Int = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "renter_id", nullable = false)
    val renter: Account,

    @NotNull
    @Column(name = "start_date", nullable = false)
    val startDate: LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Column(name = "end_date", nullable = false)
    val endDate: LocalDateTime,

    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    var status: BookingStatus,

    @Column(name = "description")
    val description: String? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "booked_items",
        joinColumns = [JoinColumn(name = "booking_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    val bookedItems: Set<Item>
)
