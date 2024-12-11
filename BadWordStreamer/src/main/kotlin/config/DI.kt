package config

import message.KafkaMessageViolationStreamer
import message.MessageStreamer
import org.koin.dsl.module
import service.BanWordViolationRule
import service.ViolationRule

val appModule = module {
    single<ViolationRule> { BanWordViolationRule() }
    single<MessageStreamer> { KafkaMessageViolationStreamer(listOf(get())) }
}