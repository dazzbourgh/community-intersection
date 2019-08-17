package zhi.yest.vk.friendfinder

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@Profile("test")
class TestApplicationSecurity {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        // formatter:off
        return http
                .authorizeExchange()
                .pathMatchers("/**").permitAll()
                .and()
                .httpBasic().disable()
                .build()
        // formatter:on
    }
}
