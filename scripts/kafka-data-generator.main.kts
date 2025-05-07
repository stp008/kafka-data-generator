#!/usr/bin/env kotlin
@file:DependsOn("org.apache.kafka:kafka-clients:3.7.0")

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import kotlin.random.Random
import java.util.Properties

val bootstrapServers = "192.168.1.65:9092"
val topicCount = 5
val partitionsPerTopic = 3
val messagesPerPartition = 10
val messageSizeBytes = 512

fun generateRandomAsciiString(size: Int): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return List(size) { chars.random() }.joinToString("")
}

fun createKafkaTopics() {
    val adminProps = Properties().apply {
        put("bootstrap.servers", bootstrapServers)
    }

    AdminClient.create(adminProps).use { adminClient ->
        val topics = (1..topicCount).map {
            NewTopic("topic-$it", partitionsPerTopic, 1.toShort())
        }

        println("Creating topics...")
        adminClient.createTopics(topics).all().get()
        println("Topics created successfully.")
    }
}

fun produceKafkaMessages() {
    val producerProps = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    }

    KafkaProducer<String, String>(producerProps).use { producer ->
        println("Producing messages...")

        for (i in 1..topicCount) {
            val topic = "topic-$i"
            for (partition in 0 until partitionsPerTopic) {
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

createKafkaTopics()
produceKafkaMessages()