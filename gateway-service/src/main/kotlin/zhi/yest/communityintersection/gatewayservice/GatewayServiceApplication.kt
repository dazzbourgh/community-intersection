package zhi.yest.communityintersection.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.security.oauth2.gateway.TokenRelayGatewayFilterFactory

@SpringBootApplication
@EnableDiscoveryClient
class GatewayServiceApplication(private val filterFactory: TokenRelayGatewayFilterFactory) {

//    @Bean
//    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
//        //@formatter:off
//        return builder.routes()
//                .route("people") { r ->
//                    r.path("/v1/people")
//                            .filters { f -> f.filter(filterFactory.apply()) }
//                            .uri("http://localhost:9098")
//                }
//                .build()
//        //@formatter:on
//    }
}

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}
