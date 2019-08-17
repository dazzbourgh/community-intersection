package zhi.yest.communityintersection.gatewayservice.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Controller
@RequestMapping("index")
class FrontController(@Value("\${vk.front.end.link}") val frontEndLink: String) {
    @GetMapping
    fun index(exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.add(HttpHeaders.LOCATION, frontEndLink)
        return response.setComplete()
    }
}
