package zhi.yest.vk.communityscanner.vk.impl

import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import zhi.yest.vk.communityscanner.vk.VkMethodExecutor

@Service
class VkMethodExecutorImpl : VkMethodExecutor {
    private val restTemplate: RestTemplate = RestTemplate()
    private val version = "5.92"
    private val accessToken: String = System.getProperty("access_token")
    private val url: String = """https://api.vk.com/method/%s?v=$version&access_token=$accessToken"""

    override fun execute(methodName: String, params: Map<String, String>): ObjectNode {
        val urlParams = params.entries
                .joinToString(prefix = "&", separator = "&") { """${it.key}=${it.value}""" }
        return restTemplate.getForObject(String.format(url, methodName) + urlParams, ObjectNode::class.java)!!
    }
}