package zhi.yest.communityintersection.peopleservice.vk

import org.springframework.security.oauth2.core.user.OAuth2User
import zhi.yest.communityintersection.peopleservice.domain.User

interface UserService {
    suspend fun search(groupId: String, fields: Map<String, String>, oAuth2User: OAuth2User): List<User>
}
