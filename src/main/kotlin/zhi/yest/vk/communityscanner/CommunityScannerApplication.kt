package zhi.yest.vk.communityscanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurerComposite

@SpringBootApplication
class CommunityScannerApplication {
    // Turn off CORS
    @Bean
    fun corsConfigurer(): WebFluxConfigurer {
        return object : WebFluxConfigurerComposite() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CommunityScannerApplication>(*args)
}
