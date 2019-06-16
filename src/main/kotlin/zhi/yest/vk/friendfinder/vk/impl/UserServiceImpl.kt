package zhi.yest.vk.friendfinder.vk.impl

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import zhi.yest.vk.friendfinder.config.security.authenticated
import zhi.yest.vk.friendfinder.config.security.dto.VkResponse
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.vk.UserService

@Service
class UserServiceImpl : UserService {
    override suspend fun search(groupId: String,
                                fields: Map<String, String>,
                                oAuth2User: OAuth2User): List<User> =
            WebClient.builder()
                    .filter(authenticated(oAuth2User))
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
                    .awaitSingle()
}
