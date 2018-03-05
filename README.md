<p>Start Zookeeper</p>

<p>Start Kafka</p>

<p>Create topic</p>
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic netty_kafka_topic

<p>Go to project folder and run</p>
mvn clean compile assembly:single

<p>Go to target folder and run</p>
java -jar com.cmt.poc.nettykafka-0.0.1-SNAPSHOT-jar-with-dependencies.jar

<p>—— Application will be started</p>

<p>Test on Postman REST client on chrome</p>
Making request to send the message to Kafka 
POST : http://localhost:8080/message
Body:
{
	"name":"abc"
}

Making request to get the message from Kafka
GET : http://localhost:8080/message
Response:
{
    "protobuffs": [
        {
            "name": "abc"
        },
        {
            "name": "xyz"
        }
    ]
}
