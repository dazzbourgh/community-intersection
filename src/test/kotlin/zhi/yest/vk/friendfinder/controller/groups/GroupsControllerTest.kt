package zhi.yest.vk.friendfinder.controller.groups

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import zhi.yest.vk.friendfinder.config.security.dto.VkError
import zhi.yest.vk.friendfinder.config.security.dto.VkException
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.vk.GroupsService

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GroupsControllerTest {

    @Autowired
    private val webTestClient: WebTestClient? = null

    @MockBean
    private val groupsService: GroupsService? = null

    @Test
    fun getGroupInfo_groupExists_returnsGroup() = runBlocking {
        given(groupsService!!.findById(eq("123"), any()))
                .willReturn(Group(123, "name", "screenName", false, "group",
                        false, false, false,
                        "description", null, null, null))

        webTestClient!!.get().uri("/groups/123")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.name").isEqualTo("name")
    }

    @Test
    fun getGroupInfo_groupDoesNotExists_returns404() = runBlocking<String> {
        given(groupsService!!.findById(eq("123"), any()))
                .willThrow(VkException("Group not found", VkError(100, "not found")))

        webTestClient!!.get().uri("/groups/123")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(String::class.java)
                .consumeWith<Nothing> {
                    body -> assertThat(body).isEqualTo("Group not found")
                }
    }

    @Test
    fun getGroupInfo_unknownError_returns500() = runBlocking<String> {
        given(groupsService!!.findById(eq("123"), any()))
                .willThrow(VkException("Unknown", null))

        webTestClient!!.get().uri("/groups/123")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is5xxServerError
                .expectBody(String::class.java)
                .consumeWith<Nothing> {
                    body -> assertThat(body).isEqualTo("Unknown")
                }
    }
}