package zhi.yest.vk.friendfinder.filter

import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono
import java.net.URI

fun vkApiFilter(token: String,
                vkApiVersion: String) =
        ExchangeFilterFunction.ofRequestProcessor {
            val delimiter = if (it.url().toString().contains("?")) "&" else "?"
            Mono.just(ClientRequest.from(it)
                    .url(URI("${it.url()}${delimiter}access_token=$token&v=$vkApiVersion"))
                    .build())
        }
