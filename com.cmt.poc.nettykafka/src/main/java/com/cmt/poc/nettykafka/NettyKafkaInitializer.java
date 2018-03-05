/**
 * 
 */
package com.cmt.poc.nettykafka;

import org.apache.kafka.clients.producer.KafkaProducer;

import com.cmt.poc.nettykafka.modal.Protobuff;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * @author c.tripathi
 *
 */
public class NettyKafkaInitializer extends ChannelInitializer<SocketChannel> {

	private KafkaProducer<String, Protobuff> kafkaProducer;

	public NettyKafkaInitializer(KafkaProducer<String, Protobuff> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(new JsonObjectDecoder());
		p.addLast(new HttpObjectAggregator(1048576));
		p.addLast(new NettyKafkaServerHandler(kafkaProducer));
	}
}
