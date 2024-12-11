package message

import io.klogging.NoCoLogging
import io.slurm.clients.user.message.UserMessage
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.Properties

class KafkaMessageSender : MessageSender<UserMessage> {

    private val producerProperties: Properties by lazy {
        val props = Properties()
        props.load(
            javaClass.getResourceAsStream("/$PROPERTY_FILE_NAME")
        )
        props
    }
    private val producer: KafkaProducer<String, UserMessage> = KafkaProducer(producerProperties)

    override fun sendMessage(message: UserMessage) {
        val record: ProducerRecord<String, UserMessage> = ProducerRecord(
            producerProperties.getProperty(TOPIC_PROPERTY_NAME),
            message.userId.toString(),
            message
        )
        logger.info { "Sending message from ${message.userId}: ${message.message}" }
        producer.send(record)
    }

    private companion object : NoCoLogging {
        const val PROPERTY_FILE_NAME = "producer.properties"
        const val TOPIC_PROPERTY_NAME = "app.user.message.topic.name"
    }
}