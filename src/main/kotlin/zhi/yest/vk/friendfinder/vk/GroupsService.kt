package zhi.yest.vk.friendfinder.vk

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import zhi.yest.vk.friendfinder.domain.Group

interface GroupsService {
    suspend fun findById(groupId: String, token: OAuth2AuthenticationToken): Group
}