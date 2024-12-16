package ru.ushakov.beansorder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class BeansOrderApplication

fun main(args: Array<String>) {
	runApplication<BeansOrderApplication>(*args)
}
