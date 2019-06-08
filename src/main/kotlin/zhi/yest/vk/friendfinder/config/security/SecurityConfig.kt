package zhi.yest.vk.friendfinder.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationRequestRedirectWebFilter
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class SecurityConfig {
    @Bean
    fun configure(http: ServerHttpSecurity,
                  resolver: ServerOAuth2AuthorizationRequestResolver,
                  authManager: ReactiveAuthenticationManager
    ): SecurityWebFilterChain {

        return http.authorizeExchange()
                .anyExchange().authenticated()
                .and().oauth2Login()
                .authenticationManager(authManager)
                .and().addFilterAt(OAuth2AuthorizationRequestRedirectWebFilter(resolver), SecurityWebFiltersOrder.FIRST)
                .build()
    }

    @Bean
    fun webClient(clientRegistrationRepo: ReactiveClientRegistrationRepository,
                  repository: ServerOAuth2AuthorizedClientRepository): WebClient {
        val filter = ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepo,
                repository)
        return WebClient.builder().filter(filter).build()
    }

    @Bean
    fun authManager(vkCodeTokenResponseClient: VkCodeTokenResponseClient): ReactiveAuthenticationManager {
        // TODO: fetch user details
        // TODO: get real userId
        return OAuth2LoginReactiveAuthenticationManager(vkCodeTokenResponseClient, ReactiveOAuth2UserService {
            Mono.just(DefaultOAuth2User(
                    mutableListOf(OAuth2UserAuthority(mutableMapOf("some" to "attribute" as Any))),
                    mutableMapOf("id" to "123" as Any),
                    "id"))
        })
    }
}
