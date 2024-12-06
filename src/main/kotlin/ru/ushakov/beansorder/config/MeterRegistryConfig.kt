package ru.ushakov.beansorder.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MeterRegistryConfig {

    @Bean
    fun customMeterRegistry(): MeterRegistry {
        val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
        prometheusMeterRegistry.config().commonTags("application", "beans-order")

        prometheusMeterRegistry.config().meterFilter(
            MeterFilter.denyNameStartsWith("jvm.gc")
        )

        return prometheusMeterRegistry
    }
}