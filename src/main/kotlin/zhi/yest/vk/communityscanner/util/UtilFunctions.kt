package zhi.yest.vk.communityscanner.util

fun String.trimQuotes(): String {
    return this.replace("\"", "")
}