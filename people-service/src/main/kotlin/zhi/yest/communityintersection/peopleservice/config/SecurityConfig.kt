package zhi.yest.communityintersection.peopleservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.WebFilter
import zhi.yest.communityintersection.peopleservice.security.VkJwtDecoder

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        // @formatter:off
        http
                .authorizeExchange()
                .anyExchange()
                    .authenticated()
                .and()
                .addFilterAt(WebFilter { exchange, chain ->
                    exchange.request.headers.forEach { println(it) }
                    println()
                    chain.filter(exchange)
                }, SecurityWebFiltersOrder.FIRST)
                //TODO: enable CSRF protection
                .csrf()
                    .disable()
                .httpBasic()
                    .disable()
                .oauth2ResourceServer()
                    .jwt()
                        .jwtDecoder(VkJwtDecoder())
        return http.build()
        // @formatter:on
    }
}
