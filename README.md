
# Introduction

This project deals with the collection and streaming of data for Advanced Driver Assistance Systems (ADAS). The data comprises three types: images, audio in WAV format for Speech Emotion Recognition, and structured data in CSV format for Driver Behavior analysis. To facilitate this, we have developed two microservices: adas-kafkaProducer and adas-datastream.

## adas-kafkaProducer
### Purpose

The adas-kafkaProducer microservice serves as a data producer. It's responsible for periodically scanning specified directories for all three types of data files. These files are then encoded into base64 format and encapsulated in JSON messages, which are subsequently sent to a designated Kafka topic. This asynchronous process allows for seamless data transfer.

### Workflow

File Detection: The microservice scans designated directories for image, WAV, and CSV files.
Encoding: Detected files are encoded into base64 format, ensuring their readiness for transmission.
Message Packaging: The base64-encoded files are encapsulated in JSON messages, preserving their type and content.
Kafka Publishing: These JSON messages are sent to a Kafka topic, enabling further processing downstream.

## adas-datastream
### Purpose

The adas-datastream component is designed to work in conjunction with adas-kafkaProducer. It serves as a Kafka consumer, listening on the same Kafka topic where the producer publishes data. Upon receiving messages, it verifies the file type and subsequently stores the data in an Amazon S3 bucket named according to the file type.

### Workflow

Kafka Consumption: This microservice continuously listens to the specified Kafka topic, awaiting incoming data messages.
Message Verification: Upon receiving a message, it identifies the type of data based on the message content.
Data Storage: The data is then stored in an Amazon S3 bucket that corresponds to its type. For example, all image files are placed in the 'image' bucket within Amazon S3.
By utilizing these two microservices, the project enables seamless streaming of ADAS data to Amazon S3. Data consumers can access and utilize this data from the appropriate S3 bucket, ensuring efficient and organized data management.

# Contributors
Koustav Naha Roy

# License
This project is licensed under Koustav. For more details, please refer to the LICENSE file.

# Acknowledgments
We would like to acknowledge the contributions of the open-source community and the support of our sponsors in making this project possible. Thank you for your support!
