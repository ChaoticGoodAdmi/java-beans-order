package ru.ushakov.beansorder.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ushakov.beansorder.controller.*
import ru.ushakov.beansorder.domain.Order
import ru.ushakov.beansorder.domain.OrderItem
import ru.ushakov.beansorder.domain.OrderStatus
import ru.ushakov.beansorder.kafka.OrderEventProducer
import ru.ushakov.beansorder.repository.OrderRepository
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderEventProducer: OrderEventProducer
) {

    @Transactional
    fun createOrder(userId: String, coffeeShopId: String, request: CreateOrderRequest): CreateOrderResponse {
        val order = Order(
            userId = userId,
            coffeeShopId = coffeeShopId,
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
        orderEventProducer.sendOrderUpdatedEvent(order)
        return UpdateOrderStatusResponse(
            orderId = order.id,
            status = order.status
        )
    }

    fun getOrdersByUser(userId: String): List<OrderResponse> {
        val orders = orderRepository.findByUserId(userId)

        return orders
            .sortedWith(
                compareBy<Order>
                { order ->
                    when (order.status) {
                        OrderStatus.READY -> 0
                        OrderStatus.IN_PROGRESS -> 1
                        OrderStatus.CREATED -> 2
                        OrderStatus.DELIVERED -> 3
                    }
                }.thenByDescending { it.createdAt }
            )
            .map { order -> mapToOrderResponse(order) }
    }

    private fun calculateTotalCost(request: CreateOrderRequest): BigDecimal {
        val itemListCost = request.items.sumOf { it.price * BigDecimal(it.quantity) }
        return itemListCost.subtract(BigDecimal(request.bonusPointsForPayment))
    }

    fun getOrderById(orderId: Long, userId: String): OrderResponse {
        return orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order $orderId not found") }
            .takeIf { it.userId == userId }
            ?.let { mapToOrderResponse(it) }
            ?: throw IllegalArgumentException("Access denied: Order $orderId does not belong to user $userId")
    }

    fun getOrdersByCoffeeShop(coffeeShopId: String): Map<String, List<OrderResponse>> {
        val coffeeShopOrders = orderRepository.findByCoffeeShopIdAndStatusNot(coffeeShopId, OrderStatus.DELIVERED)
        val needToDeliver = coffeeShopOrders
            .filter { it.status == OrderStatus.READY }
            .sortedBy { it.createdAt }
            .map { mapToOrderResponse(it) }
        val needToFinish = coffeeShopOrders
            .filter { it.status == OrderStatus.IN_PROGRESS }
            .sortedBy { it.createdAt }
            .map { mapToOrderResponse(it) }
        val needToPrepare = coffeeShopOrders
            .filter { it.status == OrderStatus.CREATED }
            .sortedBy { it.createdAt }
            .map { mapToOrderResponse(it) }
        return mapOf(
            "needToDeliver" to needToDeliver,
            "needToFinish" to needToFinish,
            "needToPrepare" to needToPrepare
        )
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
