package service

import io.slurm.clients.user.message.UserMessage
import java.io.FileNotFoundException

class BanWordViolationRule : ViolationRule {

    private val bannedWords: Set<String> = javaClass.getResource("/$BANNED_WORDS_FILE_NAME")
        ?.readText()
        ?.split(regex = Regex("\\n"))
        ?.toSet()
        ?: throw FileNotFoundException("Resource file '$BANNED_WORDS_FILE_NAME' not found.")

    override fun test(userMessage: UserMessage): List<String> =
        userMessage.message.trim().split(regex = WORD_REGEX)
            .filter { it.isNotBlank() }
            .filter { it in bannedWords }

    private companion object {
        const val BANNED_WORDS_FILE_NAME = "banned_words.txt"
        val WORD_REGEX = Regex("\\P{L}+")
    }
}