package zhi.yest.vk.friendfinder.config.security

import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class VkAuthorizationRequestResolver(
        clientRegistrationRepository: ReactiveClientRegistrationRepository) : ServerOAuth2AuthorizationRequestResolver {
    private val defaultAuthorizationRequestResolver: ServerOAuth2AuthorizationRequestResolver

    init {
        this.defaultAuthorizationRequestResolver = DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository)
    }

    override fun resolve(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        return this.defaultAuthorizationRequestResolver.resolve(exchange)
                .addParameters()
    }

    override fun resolve(exchange: ServerWebExchange, clientRegistrationId: String): Mono<OAuth2AuthorizationRequest> {
        return this.defaultAuthorizationRequestResolver.resolve(
                exchange, clientRegistrationId)
                .addParameters()
    }

    private fun Mono<OAuth2AuthorizationRequest>.addParameters() =
            this.map { request ->
                val additionalParameters = mapOf("v" to "5.95",
                        "scope" to "wall,offline",
                        "registration_id" to "vk")
                OAuth2AuthorizationRequest.from(request)
                        .additionalParameters(additionalParameters)
                        .build()
            }

}
