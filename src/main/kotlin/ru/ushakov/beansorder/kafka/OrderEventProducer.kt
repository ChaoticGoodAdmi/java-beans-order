package ru.ushakov.beansorder.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import ru.ushakov.beansorder.domain.Order
import ru.ushakov.beansorder.domain.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class OrderEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) {

    fun sendOrderCreatedEvent(order: Order) {
        val event = objectMapper.writeValueAsString(toKafkaDTO(order))
        kafkaTemplate.send("OrderCreated", event)
        println("OrderCreatedEvent sent to Kafka: $event")
    }

    fun sendOrderUpdatedEvent(order: Order) {
        val event = objectMapper.writeValueAsString(OrderUpdatedKafkaDto(order.id, order.userId, order.status))
        kafkaTemplate.send("OrderUpdated", event)
        println("OrderUpdatedEvent sent to Kafka: $event")
    }

    private fun toKafkaDTO(order: Order): OrderKafkaDTO {
        return OrderKafkaDTO(
            orderId = order.id,
            userId = order.userId,
            coffeeShopId = order.coffeeShopId,
            items = order.items.map {
                OrderItemKafkaDTO(
                    productId = it.productId,
                    quantity = it.quantity,
                    price = it.price
                )
            },
            totalCost = order.totalCost,
            createdAt = order.createdAt,
            status = order.status,
            bonusPointsUsed = order.bonusPointsUsed
        )
    }
}

data class OrderKafkaDTO(
    val orderId: Long,
    val userId: String,
    val coffeeShopId: String,
    val items: List<OrderItemKafkaDTO>,
    val totalCost: BigDecimal,
    val createdAt: LocalDateTime,
    val status: OrderStatus,
    val bonusPointsUsed: Int
)

data class OrderItemKafkaDTO(
    val productId: String,
    val quantity: Int,
    val price: BigDecimal
)

data class OrderUpdatedKafkaDto(
    val orderId: Long,
    val userId: String,
    val newStatus: OrderStatus,
)

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}