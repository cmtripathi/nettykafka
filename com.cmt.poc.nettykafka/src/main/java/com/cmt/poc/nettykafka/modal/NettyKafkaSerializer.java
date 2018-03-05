package com.cmt.poc.nettykafka.modal;

import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.common.serialization.Serializer;

/**
 * 
 * @author c.tripathi
 *
 */
public class NettyKafkaSerializer implements Serializer<Protobuff> {

	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	public byte[] serialize(String topic, Protobuff data) {
		return SerializationUtils.serialize(data);
	}

	public void close() {
		
	}

}
