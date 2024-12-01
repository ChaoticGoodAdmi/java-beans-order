package ru.ushakov.beansorder.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ushakov.beansorder.domain.Order
import ru.ushakov.beansorder.domain.OrderItem

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserId(userId: String): List<Order>
}

interface OrderItemRepository : JpaRepository<OrderItem, Long>