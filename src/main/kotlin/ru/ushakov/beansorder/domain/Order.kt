package ru.ushakov.beansorder.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "orders",
    indexes = [Index(name = "idx_userId", columnList = "userId")]
)
data class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: String = String(),

    @Column(nullable = false)
    val coffeeShopId: String = String(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var items: List<OrderItem> = listOf(),

    @Column(nullable = false)
    val totalCost: BigDecimal = BigDecimal.ZERO,

    @Column
    val bonusPointsUsed: Int = 0,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var finishedAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.CREATED
)