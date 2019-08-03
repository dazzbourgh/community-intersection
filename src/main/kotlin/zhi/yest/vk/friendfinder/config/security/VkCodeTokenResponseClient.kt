package zhi.yest.vk.friendfinder.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import zhi.yest.vk.friendfinder.config.security.dto.VkOAuth2AccessTokenResponse

@Component
class VkCodeTokenResponseClient(@Value("\${spring.security.oauth2.client.registration.vk.client-id}")
                                private val clientId: String,
                                @Value("\${spring.security.oauth2.client.registration.vk.client-secret}")
                                private val clientSecret: String,
                                private val webClient: WebClient) : ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    override fun getTokenResponse(authorizationGrantRequest: OAuth2AuthorizationCodeGrantRequest): Mono<OAuth2AccessTokenResponse> {
        return Mono.defer {
            val clientRegistration = authorizationGrantRequest.clientRegistration

            val authorizationExchange = authorizationGrantRequest.authorizationExchange
            val authorizationResponse = authorizationExchange.authorizationResponse
            val redirectUri = authorizationExchange.authorizationRequest.redirectUri
            val tokenUri = clientRegistration.providerDetails.tokenUri +
                    mapOf("client_id" to clientId,
                            "client_secret" to clientSecret,
                            "redirect_uri" to redirectUri,
                            "code" to authorizationResponse.code)
                            .map { "${it.key}=${it.value}" }
                            .joinToString(separator = "&", prefix = "?")

            webClient.get()
                    .uri(tokenUri)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .flatMap { response -> response.body(BodyExtractors.toMono(VkOAuth2AccessTokenResponse::class.java)) }
                    .map { vkOAuth2AccessTokenResponse ->
                        OAuth2AccessTokenResponse.withToken(vkOAuth2AccessTokenResponse.accessToken)
                                .expiresIn(vkOAuth2AccessTokenResponse.expiresIn.toLong())
                                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                                .scopes(setOf("wall", "offline"))
                                .build()
                    }
        }
    }
}
