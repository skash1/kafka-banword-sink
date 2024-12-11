package message

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.klogging.NoCoLogging
import io.slurm.clients.user.message.UserMessage
import io.slurm.clients.user.message.violation.UserMessageViolation
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import service.ViolationRule
import java.util.Properties

class KafkaMessageViolationStreamer(
    private val violationRules: List<ViolationRule>
) : MessageStreamer {

    private val streamerProperties: Properties by lazy {
        val props = Properties()
        props.load(
            javaClass.getResourceAsStream("/$PROPERTY_FILE_NAME")
        )
        props
    }
    private val serdeConfig: Map<String, String> = mapOf(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to
        streamerProperties.getProperty(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG)
    )

    override fun stream() {
        val userMessageSerde = getSerde<UserMessage>()
        val messageViolationSerde = getSerde<UserMessageViolation>()
        val sourceTopicName = streamerProperties.getProperty(SOURCE_TOPIC_PROPERTY_NAME)
        val targetTopicName = streamerProperties.getProperty(TARGET_TOPIC_PROPERTY_NAME)

        val streamsBuilder = StreamsBuilder()
        streamsBuilder.stream(sourceTopicName, Consumed.with(Serdes.String(), userMessageSerde))
            .mapValues { userMessage ->
                violationRules.flatMap { it.test(userMessage) }
            }
            .filter { _, violations -> violations.isNotEmpty() }
            .flatMap { userId, violations -> violations.map { KeyValue.pair(userId, it) } }
            .map { userId, violation -> KeyValue.pair(userId, UserMessageViolation(violation, userId.toLong())) }
            .peek { userId, _ -> logger.info { "Found violation for user $userId" } }
            .to(targetTopicName, Produced.with(Serdes.String(), messageViolationSerde))

        val topology = streamsBuilder.build()
        println(topology.describe())

        val kafkaStreams = KafkaStreams(topology, streamerProperties)
        kafkaStreams.start()
        Runtime.getRuntime().addShutdownHook(Thread { kafkaStreams.close() })
    }

    private fun <T : SpecificRecord> getSerde(): SpecificAvroSerde<T> = SpecificAvroSerde<T>().apply {
        configure(serdeConfig, false)
    }

    private companion object : NoCoLogging {
        const val PROPERTY_FILE_NAME = "stream.properties"
        const val SOURCE_TOPIC_PROPERTY_NAME = "app.stream.source.topic.name"
        const val TARGET_TOPIC_PROPERTY_NAME = "app.stream.target.topic.name"
    }
}