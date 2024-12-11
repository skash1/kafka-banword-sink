package generator

import io.slurm.clients.user.message.UserMessage

fun interface MessageGenerator {
    fun generateNext(): UserMessage
}