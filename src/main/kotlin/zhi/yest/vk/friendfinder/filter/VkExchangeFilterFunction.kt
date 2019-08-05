package zhi.yest.vk.friendfinder.filter

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.net.URI

@Component
class VkExchangeFilterFunction(@Value("\${vk.api.version}")
                               private val vkApiVersion: String) : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(RuntimeException("No authentication found for current request.")))
                .map { it.authentication.principal }
                .cast(DefaultOAuth2User::class.java)
                .map {
                    ClientRequest.from(request)
                            .url(URI.create("${request.url()}&v=$vkApiVersion&access_token=${it.attributes["token"]}"))
                            .build()
                }
                .flatMap { next.exchange(it) }
    }
}
