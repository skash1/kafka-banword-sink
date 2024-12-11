package service

import io.slurm.clients.user.message.UserMessage

fun interface ViolationRule {
    fun test(userMessage: UserMessage): List<String>
}