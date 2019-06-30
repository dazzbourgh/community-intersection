package zhi.yest.communityintersection.uaaservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableOAuth2Sso
class UaaServiceApplication

fun main(args: Array<String>) {
    runApplication<UaaServiceApplication>(*args)
}
