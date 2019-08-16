package zhi.yest.vk.friendfinder.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class VkResponse<T>(
        @JsonProperty("response")
        val response: List<T>?, val error: VkError?)

data class VkError(@JsonProperty("error_code") val errorCode: Int,
                   @JsonProperty("error_msg") val errorMessage: String)

class VkException(message: String, val error: VkError?) : RuntimeException(message)
