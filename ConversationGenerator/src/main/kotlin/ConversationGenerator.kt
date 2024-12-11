import config.appModule
import generator.MessageGenerator
import io.klogging.NoCoLogging
import io.slurm.clients.user.message.UserMessage
import message.MessageSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import kotlin.random.Random
import kotlin.random.nextInt

class Application : KoinComponent {
    private val messageGenerator: MessageGenerator by inject()
    private val userMessageSender: MessageSender<UserMessage> by inject()

    fun emulateVividConversation() {
        while (true) {
            runCatching {
                userMessageSender.sendMessage(
                    messageGenerator.generateNext()
                )
                Thread.sleep(randomDelay())
            }.onFailure {
                logger.error(it) { "Unable to send user message. Will attempt in a while.." }
                Thread.sleep(ON_FAILURE_SENDING_ATTEMPT_DELAY_MS)
            }
        }
    }

    private fun randomDelay() = Random.nextInt(MESSAGE_SENDING_DELAY_RANGE_MS).toLong()

    private companion object : NoCoLogging {
        val MESSAGE_SENDING_DELAY_RANGE_MS = 100..1000
        const val ON_FAILURE_SENDING_ATTEMPT_DELAY_MS = 3000L
    }
}

fun main() {
    startKoin { modules(appModule) }

    Application().emulateVividConversation()
}