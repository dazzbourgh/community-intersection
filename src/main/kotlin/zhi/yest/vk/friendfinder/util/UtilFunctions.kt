package zhi.yest.vk.friendfinder.util

fun String.trimQuotes(): String {
    return this.replace("\"", "")
}