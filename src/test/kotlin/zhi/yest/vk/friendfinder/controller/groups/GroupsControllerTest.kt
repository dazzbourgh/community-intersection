package zhi.yest.vk.friendfinder.controller.groups

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.vk.GroupsService

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebclientDemoApplicationTests {

    @Autowired
    private val webTestClient: WebTestClient? = null

    @MockBean
    private val groupsService: GroupsService? = null

    @Test
    fun test1CreateGithubRepository() = runBlocking {
        given(groupsService!!.findById(eq("123"), any()))
                .willReturn(Group(123, "name", "screenName", false, "group",
                        false, false, false,
                        "description", null, null, null))

        webTestClient!!.get().uri("/groups/123")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.name").isEqualTo("name")
    }
}