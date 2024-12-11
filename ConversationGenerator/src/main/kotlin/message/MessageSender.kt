package message

fun interface MessageSender<T> {
    fun sendMessage(message: T)
}