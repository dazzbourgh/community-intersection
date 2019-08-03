package zhi.yest.vk.friendfinder.config.security.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class VkResponse<T>(val response: List<T>?, val error: VkError?)
data class VkUserInfo(val id: Long,
                      @JsonAlias("first_name")
                      val firstName: String,
                      @JsonAlias("last_name")
                      val lastName: String)

data class VkError(@JsonProperty("error_code") val errorCode: Int,
                   @JsonProperty("error_msg") val errorMessage: String)

class VkException(message: String, val error: VkError?) : RuntimeException(message)

class VkOAuth2AccessTokenResponse(
        @JsonAlias("access_token")
        val accessToken: String,
        @JsonAlias("expires_in")
        val expiresIn: String)
