package zhi.yest.vk.friendfinder.config.security

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component("vkClientRepository")
class VkClientRepository : ServerOAuth2AuthorizedClientRepository {
    override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(clientRegistrationId: String?, principal: Authentication?, exchange: ServerWebExchange?): Mono<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAuthorizedClient(clientRegistrationId: String?, principal: Authentication?, exchange: ServerWebExchange?): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveAuthorizedClient(authorizedClient: OAuth2AuthorizedClient?, principal: Authentication?, exchange: ServerWebExchange?): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
