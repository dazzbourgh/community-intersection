package zhi.yest.communityintersection.peopleservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                //TODO: enable CSRF protection
                .csrf().disable()
                .httpBasic().disable()
                .oauth2ResourceServer()
                .bearerTokenConverter { exchange ->
                    exchange.request.headers.forEach { header ->
                        println(header)
                    }
                    Mono.empty()
                }
                .and()
                .build()
    }
}
