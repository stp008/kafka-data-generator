package com.stp008.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

fun main() {
    val bootstrapServers = "localhost:9092"

    val topicCount = 11
    val partitionsPerTopic = 20
    val messagesPerPartition = 100
    val messageSizeBytes = 1_000_000

    createKafkaTopics(bootstrapServers, topicCount, partitionsPerTopic)
    produceKafkaMessages(
        bootstrapServers,
        topicCount,
        partitionsPerTopic,
        messagesPerPartition,
        messageSizeBytes
    )
}

/**
 * Creates the specified number of Kafka topics with the given partition count.
 */
fun createKafkaTopics(bootstrapServers: String, topicCount: Int, partitions: Int) {
    val adminProps = Properties().apply {
        put("bootstrap.servers", bootstrapServers)
    }

    AdminClient.create(adminProps).use { adminClient ->
        val topics = (1..topicCount).map { i ->
            NewTopic("topic-$i", partitions, 1.toShort())
        }

        println("Creating $topicCount topics...")
        adminClient.createTopics(topics).all().get()
        println("Topics created successfully.")
    }
}

/**
 * Produces random messages of specified size to each partition in each topic.
 */
fun produceKafkaMessages(
    bootstrapServers: String,
    topicCount: Int,
    partitions: Int,
    messagesPerPartition: Int,
    messageSizeBytes: Int
) {
    val producerProps = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    }

    KafkaProducer<String, String>(producerProps).use { producer ->
        println("Producing messages...")

        for (i in 1..topicCount) {
            val topic = "topic-$i"

            for (partition in 0 until partitions) {
                for (msgIndex in 1..messagesPerPartition) {
                    val key = "key-$partition-$msgIndex"
                    val value = generateRandomAsciiString(messageSizeBytes)
                    val record = ProducerRecord(topic, partition, key, value)
                    producer.send(record)
                }

                println("  → Sent $messagesPerPartition messages to partition $partition of $topic")
            }
        }

        producer.flush()
        println("All messages have been sent.")
    }
}

/**
 * Generates a random ASCII string of a given byte size.
 */
fun generateRandomAsciiString(size: Int): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val builder = StringBuilder(size)
    repeat(size) {
        builder.append(chars.random())
    }
    return builder.toString()
}
