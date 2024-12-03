package ru.ushakov.beansorder.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ushakov.beansorder.controller.OrderResponse
import ru.ushakov.beansorder.domain.Order
import ru.ushakov.beansorder.domain.OrderItem
import ru.ushakov.beansorder.domain.OrderStatus

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserId(userId: String): List<Order>
    fun findByCoffeeShopIdAndStatusNot(coffeeShopId: String, orderStatus: OrderStatus): List<Order>
}