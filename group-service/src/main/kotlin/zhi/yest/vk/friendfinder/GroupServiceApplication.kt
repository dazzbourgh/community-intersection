package zhi.yest.vk.friendfinder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("zhi.yest")
class GroupServiceApplication

fun main(args: Array<String>) {
    runApplication<GroupServiceApplication>(*args)
}
