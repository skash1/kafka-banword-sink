package generator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.slurm.clients.user.message.UserMessage
import java.io.FileNotFoundException

class UserMessageGenerator(
    private val objectMapper: ObjectMapper
) : MessageGenerator {

    private val messages: List<UserMessage> = javaClass.getResource("/$MESSAGES_INPUT_FILE_NAME")
        ?.readText()
        ?.let { objectMapper.readValue(it) }
        ?: throw FileNotFoundException("Resource file '$MESSAGES_INPUT_FILE_NAME' not found.")
    private var currentMessageIndex = 0

    override fun generateNext(): UserMessage {
        currentMessageIndex++
        if (currentMessageIndex >= messages.size) {
            currentMessageIndex = 0
        }

        return messages[currentMessageIndex]
    }

    private companion object {
        const val MESSAGES_INPUT_FILE_NAME = "conversation.json"
    }

}