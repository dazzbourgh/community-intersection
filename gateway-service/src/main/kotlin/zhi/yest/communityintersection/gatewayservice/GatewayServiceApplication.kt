package zhi.yest.communityintersection.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableDiscoveryClient
class GatewayServiceApplication(private val filterFactory: TokenRelayGatewayFilterFactory) {

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        //@formatter:off
        return builder.routes()
                .route("people") { r ->
                    r.path("/v1/people")
                            .filters { f -> f.filter(filterFactory.apply()) }
                            .uri("lb://people-service")
                }
                .build()
        //@formatter:on
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}
