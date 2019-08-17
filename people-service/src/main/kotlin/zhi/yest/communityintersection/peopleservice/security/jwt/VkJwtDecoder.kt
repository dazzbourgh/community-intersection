package zhi.yest.communityintersection.peopleservice.security.jwt

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.temporal.ChronoUnit

class VkJwtDecoder : ReactiveJwtDecoder {
    override fun decode(token: String?): Mono<Jwt> {
        // TODO: complete this once gateway sends full details
        return Mono.just(Jwt(token, Instant.now(), Instant.now().plus(10, ChronoUnit.DAYS), mapOf("some" to "header"), mapOf("sub" to "To Do")))
    }
}
