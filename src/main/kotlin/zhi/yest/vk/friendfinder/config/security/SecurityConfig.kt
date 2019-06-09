package zhi.yest.vk.friendfinder.config.security

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
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
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.toMono
import zhi.yest.vk.friendfinder.config.security.dto.VkResponse
import zhi.yest.vk.friendfinder.config.security.dto.VkUser


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
    fun webClient(reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository,
                  serverOAuth2AuthorizedClientRepository: ServerOAuth2AuthorizedClientRepository,
                  vkCodeTokenResponseClient: VkCodeTokenResponseClient): WebClient {
        val filter = ServerOAuth2AuthorizedClientExchangeFilterFunction(
                reactiveClientRegistrationRepository,
                serverOAuth2AuthorizedClientRepository)
        return WebClient.builder().filter(filter).build()
    }

    @Bean
    fun authManager(vkCodeTokenResponseClient: VkCodeTokenResponseClient,
                    webClient: WebClient,
                    clientProperties: OAuth2ClientProperties): ReactiveAuthenticationManager {
        return OAuth2LoginReactiveAuthenticationManager(vkCodeTokenResponseClient, ReactiveOAuth2UserService { oAuth2UserRequest ->
            webClient.get()
                    .uri(clientProperties.provider["vk"]
                            ?.userInfoUri!!
                            //TODO: externalize hardcoded version
                            + "?access_token=${oAuth2UserRequest.accessToken.tokenValue}&v=5.95")
                    .exchange()
                    .flatMap { it.bodyToFlux<VkResponse<VkUser>>().toMono() }
                    .map { it.response[0] }
                    .map {
                        DefaultOAuth2User(
                                mutableListOf(OAuth2UserAuthority(mutableMapOf("some" to "attribute" as Any))),
                                mutableMapOf("id" to it.id,
                                        "firstName" to it.firstName,
                                        "lastName" to it.lastName,
                                        "fullName" to "${it.firstName} ${it.lastName}"),
                                "fullName")
                    }
        })
    }
}
