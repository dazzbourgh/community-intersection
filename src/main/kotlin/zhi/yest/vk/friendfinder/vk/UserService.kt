package zhi.yest.vk.friendfinder.vk

import org.springframework.security.oauth2.core.user.OAuth2User
import zhi.yest.vk.friendfinder.domain.User

interface UserService {
    suspend fun search(groupId: String, fields: Map<String, String>, oAuth2User: OAuth2User): List<User>
}
