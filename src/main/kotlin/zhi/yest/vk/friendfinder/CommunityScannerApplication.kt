package zhi.yest.vk.friendfinder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("zhi.yest")
class CommunityScannerApplication

fun main(args: Array<String>) {
    runApplication<CommunityScannerApplication>(*args)
}
