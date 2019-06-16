package zhi.yest.vk.friendfinder.config.security

import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono
import java.net.URI

fun vkApiFilter(oAuth2User: OAuth2User,
                vkApiVersion: String) =
        ExchangeFilterFunction.ofRequestProcessor {
            val delimiter = if (it.url().toString().contains("?")) "&" else "?"
            Mono.just(ClientRequest.from(it)
                    .url(URI("${it.url()}${delimiter}access_token=${oAuth2User.attributes["token"]}&v=$vkApiVersion"))
                    .build())
        }
