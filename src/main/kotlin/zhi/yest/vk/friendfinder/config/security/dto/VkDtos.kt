package zhi.yest.vk.friendfinder.config.security.dto

import com.fasterxml.jackson.annotation.JsonAlias

data class VkResponse<T>(val response: List<T>)
data class VkUser(val id: Long,
                  @JsonAlias("first_name")
                  val firstName: String,
                  @JsonAlias("last_name")
                  val lastName: String)

class VkOAuth2AccessTokenResponse(
        @JsonAlias("access_token")
        val accessToken: String?,
        @JsonAlias("expires_in")
        val expiresIn: String?)
