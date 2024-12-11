import config.appModule
import message.MessageStreamer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class Application : KoinComponent {
    private val violationStreamer: MessageStreamer by inject()

    fun run() {
        violationStreamer.stream()
    }
}

fun main() {
    startKoin { modules(appModule) }

    Application().run()
}