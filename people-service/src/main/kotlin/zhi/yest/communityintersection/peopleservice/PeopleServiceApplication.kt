package zhi.yest.communityintersection.peopleservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("zhi.yest")
class PeopleServiceApplication

fun main(args: Array<String>) {
    runApplication<PeopleServiceApplication>(*args)
}
