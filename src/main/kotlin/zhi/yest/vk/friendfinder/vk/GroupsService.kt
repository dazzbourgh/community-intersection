package zhi.yest.vk.friendfinder.vk

import org.springframework.security.oauth2.core.user.OAuth2User
import zhi.yest.vk.friendfinder.domain.Group

interface GroupsService {
    suspend fun findById(groupId: String, oAuth2User: OAuth2User): Group
}