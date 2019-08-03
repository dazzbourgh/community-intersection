package zhi.yest.vk.friendfinder.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationRequestRedirectWebFilter
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.toMono
import zhi.yest.vk.friendfinder.config.security.dto.VkResponse
import zhi.yest.vk.friendfinder.config.security.dto.VkUserInfo
import zhi.yest.vk.friendfinder.filter.VkExchangeFilterFunction

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @ConditionalOnMissingBean
    @Bean
    fun configure(http: ServerHttpSecurity,
                  resolver: ServerOAuth2AuthorizationRequestResolver,
                  authManager: ReactiveAuthenticationManager): SecurityWebFilterChain {
        return http
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .oauth2Login().authenticationManager(authManager)
                .and()
                .addFilterAt(OAuth2AuthorizationRequestRedirectWebFilter(resolver), SecurityWebFiltersOrder.FIRST)
                //TODO: enable CSRF protection
                .csrf().disable()
                .build()
    }

    @ConditionalOnMissingBean
    @Bean
    fun authManager(vkCodeTokenResponseClient: VkCodeTokenResponseClient,
                    clientProperties: OAuth2ClientProperties,
                    @Value("\${vk.api.version}")
                    vkApiVersion: String,
                    webClient: WebClient): ReactiveAuthenticationManager {
        return OAuth2LoginReactiveAuthenticationManager(vkCodeTokenResponseClient, ReactiveOAuth2UserService { oAuth2UserRequest ->
            webClient.get()
                    .uri(clientProperties.provider["vk"]
                            ?.userInfoUri!!
                            + "?access_token=${oAuth2UserRequest.accessToken.tokenValue}&v=$vkApiVersion"
                    )
                    .exchange()
                    .flatMap { it.bodyToFlux<VkResponse<VkUserInfo>>().toMono() }
                    .map { it.response!![0] }
                    .map {
                        DefaultOAuth2User(
                                mutableListOf(OAuth2UserAuthority(mutableMapOf("some" to "attribute" as Any))),
                                mutableMapOf<String, Any>("id" to it.id,
                                        "firstName" to it.firstName,
                                        "lastName" to it.lastName,
                                        "fullName" to "${it.firstName} ${it.lastName}",
                                        "token" to oAuth2UserRequest.accessToken.tokenValue),
                                "fullName")
                    }
        })
    }

    @Bean
    fun webClient(vkExchangeFilterFunction: VkExchangeFilterFunction,
                  clientRegistrations: ReactiveClientRegistrationRepository): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrations,
                UnAuthenticatedServerOAuth2AuthorizedClientRepository())
        return WebClient.builder()
                .filter(oauth)
                .build()
    }
}
