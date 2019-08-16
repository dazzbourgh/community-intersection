package zhi.yest.communityintersection.peopleservice.vk.impl

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import zhi.yest.communityintersection.peopleservice.domain.User
import zhi.yest.communityintersection.peopleservice.dto.VkResponse
import zhi.yest.communityintersection.peopleservice.filter.vkApiFilter
import zhi.yest.communityintersection.peopleservice.vk.UserService

@Service
class UserServiceImpl(@Value("\${vk.api.version}")
                      private val vkApiVersion: String) : UserService {
    override suspend fun search(groupId: String,
                                fields: Map<String, String>,
                                oAuth2User: OAuth2User): List<User> =
            WebClient.builder()
                    .filter(vkApiFilter(oAuth2User, vkApiVersion))
                    .build()
                    .get()
                    .uri { builder ->
                        builder.scheme("https")
                                .host("api.vk.com")
                                .path("method/execute.searchUsers")
                                .queryParam("groupId", groupId)
                                .queryParam("sex", fields["sex"])
                                .queryParam("city", fields["city"])
                                .queryParam("ageFrom", fields["ageFrom"])
                                .queryParam("ageTo", fields["ageTo"])
                                .build()
                    }
                    .exchange()
                    .flatMap { it.bodyToMono<VkResponse<User>>() }
                    .map { it.response }
                    .awaitSingle()!!
}
