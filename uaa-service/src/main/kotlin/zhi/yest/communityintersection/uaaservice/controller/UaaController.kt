package zhi.yest.communityintersection.uaaservice.controller

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/uaa")
class UaaController {
    @GetMapping
    fun userInfo(principal: OAuth2AuthenticationToken) = principal
}
