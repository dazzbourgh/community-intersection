package zhi.yest.vk.communityscanner.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import zhi.yest.vk.communityscanner.vk.VkMethodExecutor

@Service
class VkMethodExecutorImpl : VkMethodExecutor {
    private val webClient = WebClient.builder().baseUrl("https://api.vk.com/method/").build()
    private val version = "5.92"
    private val accessToken: String = System.getProperty("access_token")

    override fun execute(methodName: String, params: Map<String, String>): Mono<ObjectNode> {
        val urlParams = params.entries
                .joinToString(prefix = "&", separator = "&") { """${it.key}=${it.value}""" }
        return webClient.get()
                .uri("$methodName?v=$version&access_token=$accessToken$urlParams")
                .retrieve()
                .bodyToMono(ObjectNode::class.java)
    }
}