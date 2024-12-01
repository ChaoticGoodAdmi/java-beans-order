package ru.ushakov.beansorder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BeansOrderApplication

fun main(args: Array<String>) {
	runApplication<BeansOrderApplication>(*args)
}
