package zhi.yest.vk.friendfinder.config.security

import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono
import java.net.URI

fun authenticated(oAuth2User: OAuth2User) =
        ExchangeFilterFunction.ofRequestProcessor {
            val delimiter = if (it.url().toString().contains("?")) "&" else "?"
            Mono.just(ClientRequest.from(it)
                    //TODO: externalize hardcoded version
                    .url(URI("${it.url()}${delimiter}access_token=${oAuth2User.attributes["token"]}&v=5.95"))
                    .build())
        }
