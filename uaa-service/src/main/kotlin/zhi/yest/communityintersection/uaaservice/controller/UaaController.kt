package zhi.yest.communityintersection.uaaservice.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/uaa")
class UaaController {
    @GetMapping
    fun userInfo(principal: OAuth2Authentication) = principal
}
