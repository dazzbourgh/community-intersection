package zhi.yest.vk.friendfinder.filter

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

@Component
class VkExchangeFilterFunction(private val clientRepository: ReactiveOAuth2AuthorizedClientService,
                               @Value("\${vk.api.version}")
                               private val vkApiVersion: String) : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        val client = clientRepository.loadAuthorizedClient<OAuth2AuthorizedClient>("vk", "")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}