package zhi.yest.communityintersection.peopleservice.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.client.web.server.OAuth2AuthorizationRequestRedirectWebFilter
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.toMono
import zhi.yest.communityintersection.peopleservice.security.dto.VkResponse
import zhi.yest.communityintersection.peopleservice.security.dto.VkUserInfo
import java.util.Arrays

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
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
                .addFilterAt(corsFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterAt(OAuth2AuthorizationRequestRedirectWebFilter(resolver), SecurityWebFiltersOrder.FIRST)
                .build()
    }

    @Bean
    fun authManager(vkCodeTokenResponseClient: VkCodeTokenResponseClient,
                    clientProperties: OAuth2ClientProperties,
                    @Value("\${vk.api.version}")
                    vkApiVersion: String): ReactiveAuthenticationManager {
        return OAuth2LoginReactiveAuthenticationManager(vkCodeTokenResponseClient, ReactiveOAuth2UserService { oAuth2UserRequest ->
            WebClient.create().get()
                    .uri(clientProperties.provider["vk"]
                            ?.userInfoUri!!
                            + "?access_token=${oAuth2UserRequest.accessToken.tokenValue}&v=$vkApiVersion"
                    )
                    .exchange()
                    .flatMap { it.bodyToFlux<VkResponse<VkUserInfo>>().toMono() }
                    .map { it.response[0] }
                    .map {
                        DefaultOAuth2User(
                                mutableListOf(OAuth2UserAuthority(mutableMapOf("some" to "attribute" as Any))),
                                mutableMapOf("id" to it.id,
                                        "firstName" to it.firstName,
                                        "lastName" to it.lastName,
                                        "fullName" to "${it.firstName} ${it.lastName}",
                                        "token" to oAuth2UserRequest.accessToken.tokenValue),
                                "fullName")
                    }
        })
    }

    fun corsFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = Arrays.asList("http://allowed-origin.com")
        corsConfig.maxAge = 8000L
        corsConfig.addAllowedMethod("PUT")
        corsConfig.addAllowedMethod("POST")
        corsConfig.addAllowedMethod("GET")
        corsConfig.addAllowedMethod("OPTIONS")
        corsConfig.addAllowedHeader("VK-Allowed")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)

        return CorsWebFilter(source)
    }
}
