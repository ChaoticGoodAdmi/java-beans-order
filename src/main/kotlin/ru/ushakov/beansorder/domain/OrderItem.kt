package ru.ushakov.beansorder.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    val order: Order = Order(0, "", "", listOf(), BigDecimal.ZERO, 0, LocalDateTime.now(), LocalDateTime.now(), OrderStatus.CREATED),

    @Column(nullable = false)
    val productId: String = String(),

    @Column(nullable = false)
    val quantity: Int = 0,

    @Column(nullable = false)
    val price: BigDecimal = BigDecimal.ZERO
)

enum class OrderStatus {
    CREATED, IN_PROGRESS, READY, DELIVERED
}