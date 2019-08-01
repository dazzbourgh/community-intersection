package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import zhi.yest.vk.friendfinder.config.security.dto.VkException
import zhi.yest.vk.friendfinder.config.security.dto.VkResponse
import zhi.yest.vk.friendfinder.config.security.vkApiFilter
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.vk.GroupsService

@Service
class GroupsServiceImpl(@Value("\${vk.api.version}")
                        private val vkApiVersion: String) : GroupsService {

    override suspend fun findById(groupId: String, token: OAuth2AuthenticationToken): Group =
            WebClient.builder()
                    .filter(vkApiFilter(token.principal, vkApiVersion))
                    .build()
                    .get()
                    .uri { builder ->
                        builder.scheme("https")
                                .host("api.vk.com")
                                .path("method/groups.getById")
                                .queryParam("group_id", groupId)
                                .queryParam("fields", "description")
                                .build()
                    }
                    .exchange()
                    .flatMap { it.bodyToMono<VkResponse<Group>>() }
                    .map {
                        when {
                            it.error != null -> throw VkException("Group not found", it.error)
                            it.response != null -> it.response[0]
                            else -> throw VkException("Unsupported VK response", null)
                        }
                    }
                    .awaitSingle()
}