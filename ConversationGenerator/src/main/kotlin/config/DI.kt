package config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import generator.MessageGenerator
import generator.UserMessageGenerator
import io.slurm.clients.user.message.UserMessage
import message.KafkaMessageSender
import message.MessageSender
import org.koin.dsl.module

val appModule = module {
    val objectMapper = jacksonObjectMapper()

    single<MessageGenerator> { UserMessageGenerator(objectMapper) }
    single<MessageSender<UserMessage>> { KafkaMessageSender() }
}