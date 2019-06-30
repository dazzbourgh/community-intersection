package zhi.yest.communityintersection.uaaservice.config

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.oauth2.common.AuthenticationScheme
import org.springframework.stereotype.Component

@Configuration
class SecurityConfig {
    @Bean
    fun authoritiesExtractor() = AuthoritiesExtractor {
        listOf()
    }

    @Bean
    fun principalExtractor() = PrincipalExtractor {
        it
    }
//    private fun ssoFilter(): Filter {
//        val facebookFilter = OAuth2ClientAuthenticationProcessingFilter("/login/vk")
//        val facebookTemplate = OAuth2RestTemplate(facebook(), oauth2ClientContext)
//        facebookFilter.setRestTemplate(facebookTemplate)
//        val tokenServices = UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId())
//        tokenServices.setRestTemplate(facebookTemplate)
//        facebookFilter.setTokenServices(tokenServices)
//        return facebookFilter
//    }
}

@Component
class ClientSchemePostProcessor : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is AuthorizationCodeResourceDetails) {
            bean.clientAuthenticationScheme = AuthenticationScheme.query
        }
        return super.postProcessAfterInitialization(bean, beanName)
    }
}
