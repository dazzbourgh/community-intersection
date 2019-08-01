package zhi.yest.vk.friendfinder.vk

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import zhi.yest.vk.friendfinder.domain.User

interface UserService {
    suspend fun search(groupId: String, fields: Map<String, String>, token: OAuth2AuthenticationToken): List<User>
}
