package zhi.yest.vk.friendfinder.controller

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.given
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import zhi.yest.vk.friendfinder.domain.Group
import zhi.yest.vk.friendfinder.dto.VkError
import zhi.yest.vk.friendfinder.dto.VkException
import zhi.yest.vk.friendfinder.service.vk.GroupService

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GroupControllerIntegrationTest {

    @Autowired
    lateinit var context: ApplicationContext
    lateinit var webTestClient: WebTestClient
    @MockBean
    lateinit var groupService: GroupService

    @BeforeEach
    fun init() {
        webTestClient = WebTestClient.bindToApplicationContext(context)
                .build()
    }

    @Test
    fun getGroupInfo_groupExists_returnsGroup() {
        runBlocking {
            given(groupService.findById(eq("123"), anyOrNull()))
                    .willReturn(Group(123, "name", "screenName", false, "group",
                            false, false, false,
                            "description", null, null, null))

            webTestClient.get().uri("/groups/123")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.id").isEqualTo("123")
                    .jsonPath("$.name").isEqualTo("name")
        }
    }

    @Test
    fun getGroupInfo_groupDoesNotExists_returns404() {
        runBlocking {
            given(groupService.findById(eq("123"), anyOrNull()))
                    .willThrow(VkException("Group not found", VkError(100, "not found")))

            val body = webTestClient.get().uri("/groups/123")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound
                    .returnResult(String::class.java).responseBody

            StepVerifier.create(body)
                    .expectNext("Group not found")
                    .expectComplete()
                    .verify()
        }
    }

    @Test
    fun getGroupInfo_unknownError_returns500() {
        runBlocking {
            given(groupService.findById(eq("123"), anyOrNull())).willThrow(VkException("Unknown", null))

            val body = webTestClient.get().uri("/groups/123")
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus().is5xxServerError
                    .returnResult(String::class.java).responseBody

            StepVerifier.create(body)
                    .expectNext("Unknown")
                    .expectComplete()
                    .verify()
        }
    }
}
