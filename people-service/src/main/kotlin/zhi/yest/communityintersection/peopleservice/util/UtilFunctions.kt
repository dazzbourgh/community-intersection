package zhi.yest.communityintersection.peopleservice.util

fun String.trimQuotes(): String {
    return this.replace("\"", "")
}
