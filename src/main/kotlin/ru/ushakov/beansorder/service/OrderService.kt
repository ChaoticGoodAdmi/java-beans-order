package ru.ushakov.beansorder.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ushakov.beansorder.controller.*
import ru.ushakov.beansorder.domain.Order
import ru.ushakov.beansorder.domain.OrderItem
import ru.ushakov.beansorder.kafka.OrderEventProducer
import ru.ushakov.beansorder.repository.OrderRepository
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderEventProducer: OrderEventProducer
) {

    @Transactional
    fun createOrder(request: CreateOrderRequest): CreateOrderResponse {
        val order = Order(
            userId = request.userId,
            coffeeShopId = request.coffeeShopId,
            totalCost = calculateTotalCost(request),
            bonusPointsUsed = request.bonusPointsForPayment
        )

        val items = request.items.map {
            OrderItem(
                productId = it.productId,
                quantity = it.quantity,
                order = order
            )
        }
        order.items = items

        val savedOrder = orderRepository.save(order)
        orderEventProducer.sendOrderCreatedEvent(order)

        return CreateOrderResponse(
            orderId = savedOrder.id,
            status = savedOrder.status,
            createdAt = savedOrder.createdAt
        )
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, request: UpdateOrderStatusRequest): UpdateOrderStatusResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order with ID $orderId not found") }

        order.status = request.status
        orderRepository.save(order)

        return UpdateOrderStatusResponse(
            orderId = order.id,
            status = order.status
        )
    }

    fun getOrdersByUser(userId: String): List<OrderResponse> {
        val orders = orderRepository.findByUserId(userId)

        return orders.map { order -> mapToOrderResponse(order) }
    }

    private fun calculateTotalCost(request: CreateOrderRequest): BigDecimal {
        val itemListCost = request.items.sumOf { it.price * BigDecimal(it.quantity) }
        return itemListCost.subtract(BigDecimal(request.bonusPointsForPayment))
    }

    fun getOrderById(orderId: Long): OrderResponse {
        return orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order $orderId not found") }
            .let { mapToOrderResponse(it) }
    }

    private fun mapToOrderResponse(order: Order) = OrderResponse(
        orderId = order.id,
        coffeeShopId = order.coffeeShopId,
        items = order.items.map { item ->
            OrderItemResponse(
                productId = item.productId,
                quantity = item.quantity
            )
        },
        createdAt = order.createdAt.toString(),
        status = order.status
    )
}
