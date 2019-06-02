package zhi.yest.vk.friendfinder.config.security

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeReactiveAuthenticationManager
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationRequestRedirectWebFilter
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class SecurityConfig {
    @Bean
    fun configure(http: ServerHttpSecurity,
                  resolver: ServerOAuth2AuthorizationRequestResolver,
                  @Qualifier("vkClientRepository")
                  repository: ServerOAuth2AuthorizedClientRepository,
                  tokenConverter: ServerAuthenticationConverter): SecurityWebFilterChain {
        return http.authorizeExchange()
                .pathMatchers("/test").permitAll()
                .anyExchange().authenticated()
                .and().oauth2Login()
                .authenticationConverter(tokenConverter)
                .authorizedClientRepository(repository)
                .and().addFilterAt(OAuth2AuthorizationRequestRedirectWebFilter(resolver), SecurityWebFiltersOrder.FIRST)
                .build()
    }

    @Bean
    fun webClient(clientRegistrationRepo: ReactiveClientRegistrationRepository,
                  @Qualifier("vkClientRepository")
                  repository: ServerOAuth2AuthorizedClientRepository): WebClient {
        val filter = ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepo, repository)
        return WebClient.builder().filter(filter).build()
    }

    @Bean
    fun authManager(): ReactiveAuthenticationManager {
        return OAuth2AuthorizationCodeReactiveAuthenticationManager(WebClientReactiveAuthorizationCodeTokenResponseClient())
    }

    @Bean
    fun tokenConverter(@Qualifier("vkClientRepository")
                       repository: ServerOAuth2AuthorizedClientRepository): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange ->
            //TODO
            Mono.empty()
        }
    }
}
