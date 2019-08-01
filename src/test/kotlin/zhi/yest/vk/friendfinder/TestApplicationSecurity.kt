package zhi.yest.vk.friendfinder

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@Profile("test")
class TestApplicationSecurity {

    @Bean
    fun configure(http: ServerHttpSecurity,
                  resolver: ServerOAuth2AuthorizationRequestResolver,
                  authManager: ReactiveAuthenticationManager): SecurityWebFilterChain {
        return http
                .authorizeExchange()
                .pathMatchers("/**").permitAll()
                .and()
                .build()
    }
}