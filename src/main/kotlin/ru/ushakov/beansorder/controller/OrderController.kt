package ru.ushakov.beansorder.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.web.bind.annotation.*
import ru.ushakov.beansorder.service.OrderService
import ru.ushakov.beansorder.domain.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): CreateOrderResponse {
        return orderService.createOrder(request)
    }

    @PutMapping("/{orderId}/status")
    fun updateOrderStatus(
        @PathVariable orderId: Long,
        @RequestBody request: UpdateOrderStatusRequest
    ): UpdateOrderStatusResponse {
        return orderService.updateOrderStatus(orderId, request)
    }

    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: String): List<OrderResponse> {
        return orderService.getOrdersByUser(userId)
    }

    @GetMapping("/coffee-shop/{coffeeShopId}")
    fun getOrdersByCoffeeShop(@PathVariable coffeeShopId: String): Map<String, List<OrderResponse>> {
        return orderService.getOrdersByCoffeeShop(coffeeShopId)
    }

    @GetMapping("/{orderId}")
    fun getOrderById(@PathVariable orderId: Long): OrderResponse {
        return orderService.getOrderById(orderId)
    }
}

data class CreateOrderRequest(
    @NotBlank
    val userId: String,
    @NotBlank
    val coffeeShopId: String,
    @NotEmpty
    val items: List<OrderItemDTO>,
    @NotNull
    @Min(value = 0)
    val bonusPointsForPayment: Int
)

data class OrderItemDTO(
    @NotBlank
    val productId: String,
    @NotNull
    val price: BigDecimal,
    @NotNull
    val quantity: Int
)

data class CreateOrderResponse(
    val orderId: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime
)

data class UpdateOrderStatusRequest(
    @NotNull
    val status: OrderStatus
)

data class UpdateOrderStatusResponse(
    val orderId: Long,
    val status: OrderStatus
)

data class OrderResponse(
    val orderId: Long,
    val coffeeShopId: String,
    val items: List<OrderItemResponse>,
    val createdAt: String,
    val status: OrderStatus
)

data class OrderItemResponse(
    val productId: String,
    val quantity: Int
)