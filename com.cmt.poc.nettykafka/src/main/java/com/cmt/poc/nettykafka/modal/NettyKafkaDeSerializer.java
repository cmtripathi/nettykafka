/**
 * 
 */
package com.cmt.poc.nettykafka.modal;

import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author c.tripathi
 *
 */
public class NettyKafkaDeSerializer implements Deserializer<Protobuff> {

	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	public Protobuff deserialize(String topic, byte[] data) {
		return (Protobuff) SerializationUtils.deserialize(data);
	}

	public void close() {
		
	}

}
