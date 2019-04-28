package zhi.yest.vk.friendfinder.filtering

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import zhi.yest.vk.friendfinder.domain.Request
import zhi.yest.vk.friendfinder.domain.User
import zhi.yest.vk.friendfinder.util.trimQuotes
import java.util.concurrent.ConcurrentHashMap

@Configuration
class FilteringFunctionsConfiguration {
    @Bean
    @Scope("prototype")
    fun processingFunctions(): List<(User) -> (Request) -> Boolean> {
        return listOf(filterInteresting(ConcurrentHashMap()),
                ::filterByFields,
                ::filterPhotos)
    }

    @Bean
    fun processingFunctionsSupplier(applicationContext: ApplicationContext): () -> List<(User) -> (Request) -> Boolean> = {
        applicationContext.getBean("processingFunctions") as List<(User) -> (Request) -> Boolean>
    }
}

fun filterInteresting(userCountMap: MutableMap<User, Int>): (User) -> (Request) -> Boolean = { user ->
    { request ->
        userCountMap.compute(user) { _, value ->
            if (value == null) 1 else value + 1
        } == request.communities.size
    }
}

fun filterByFields(user: User): (Request) -> Boolean = { request ->
    val filterMap = request.peopleFilters
    filterMap == null || filterMap.isEmpty() || filterMap.entries
            .map { user.fields[it.key] == it.value.trimQuotes() }
            .all { it }
}

fun filterPhotos(user: User): (Request) -> Boolean = { _ ->
    user.fields["photo_200"] != null && user.fields["photo_200"] != ""
}
