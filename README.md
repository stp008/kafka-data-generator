## 📦 Kafka Data Generator

This project is a Kotlin-based Kafka utility for:

* Creating a specified number of Kafka topics
* Defining partitions per topic
* Sending random messages of configurable size to each partition

It uses the official Kafka client and is built with **Maven**.

---

### 🚀 Features

* ✅ Kafka topic creation using `AdminClient`
* ✅ Custom number of topics and partitions
* ✅ Configurable message size (in bytes)
* ✅ Random ASCII payload per message
* ✅ Sends messages directly to each partition

---

### 🧱 Requirements

* Java 17+
* Apache Kafka (running on `localhost:9092` by default)
* Kotlin 1.9+

---

### 📂 Project Structure

```
.
├── README.md
└── scripts/
        └── kafka-data-generator.main.kts
```

---

### ⚙️ Configuration

Inside `kafka-data-generator.main.kts`, modify the following variables to suit your needs:

```kotlin
val topicCount = 5               // Number of topics to create
val partitionsPerTopic = 3       // Number of partitions per topic
val messagesPerPartition = 10    // Number of messages per partition
val messageSizeBytes = 512       // Size of each message in bytes
```

Kafka is assumed to be running at:

```kotlin
val bootstrapServers = "localhost:9092"
```

Change this if your Kafka broker is elsewhere.

---

### 🛠️ Build and Run

```bash
kotlinc -script scripts/kafka-data-generator.main.kts
```

---

### 📝 Example Output

```
Creating 5 topics...
Topics created successfully.
Producing messages...
  → Sent 10 messages to partition 0 of topic-1
  → Sent 10 messages to partition 1 of topic-1
  ...
All messages have been sent.
```

---

### 📌 Notes

* Messages are composed of printable ASCII characters only.
* This script is ideal for load testing, partition validation, and producer experiments.
