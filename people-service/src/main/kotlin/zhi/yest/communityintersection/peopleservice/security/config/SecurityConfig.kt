package zhi.yest.communityintersection.peopleservice.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import zhi.yest.communityintersection.peopleservice.security.jwt.VkJwtDecoder

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
                .addFilterAt({ exchange, chain ->
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
