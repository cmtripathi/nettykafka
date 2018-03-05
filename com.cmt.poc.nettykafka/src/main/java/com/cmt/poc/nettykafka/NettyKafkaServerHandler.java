/**
 * 
 */
package com.cmt.poc.nettykafka;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

import com.cmt.poc.nettykafka.modal.Protobuff;
import com.cmt.poc.nettykafka.modal.Protobuffs;
import com.google.gson.Gson;

/**
 * @author c.tripathi
 *
 */
public class NettyKafkaServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private Logger logger = Logger.getLogger(NettyKafkaServerHandler.class);

	private KafkaProducer<String, Protobuff> kafkaProducer;

	private static final String RESPONSE = "{\"message\":\"message has been sent.\"}";
	private static final String ERROR_RESPONSE = "{\"message\":\"ERROR.\"}";
	
	private static final String URL_MESSAGE = "/message";
	
	private static final String TOPIC_NAME = "netty_kafka_topic";
	
	
	

	public NettyKafkaServerHandler(KafkaProducer<String, Protobuff> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
		FullHttpRequest httpRequest = (FullHttpRequest) msg;
		httpRequest.content().toString(CharsetUtil.UTF_8);
		
		String uri = httpRequest.uri();

		Gson gson = new Gson();
		

		if (HttpUtil.is100ContinueExpected(httpRequest)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.CONTINUE));
		}

		boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
		FullHttpResponse response;

		if (httpRequest.method().equals(HttpMethod.POST) && isURIValid(uri)) {
			Protobuff protobuff = gson.fromJson(httpRequest.content().toString(CharsetUtil.UTF_8), Protobuff.class);
			ProducerRecord<String, Protobuff> message = new ProducerRecord<String, Protobuff>(TOPIC_NAME, protobuff);
			kafkaProducer.send(message);
			response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(RESPONSE.getBytes()));
		} else if (httpRequest.method().equals(HttpMethod.GET) && isURIValid(uri)) {
			Properties kafkaProperties = new Properties();
			KafkaConsumer<String, Protobuff> consumer = null;
			try {
				kafkaProperties.load(NettyKafkaServer.class.getResourceAsStream("/consumer.properties"));
			
			consumer = new KafkaConsumer<String, Protobuff>(kafkaProperties);
			
			TopicPartition tp = new TopicPartition(TOPIC_NAME, 0);
		    List<TopicPartition> tps = Arrays.asList(tp);
		    consumer.assign(tps);
			
			Protobuffs protobuffs = new Protobuffs();
			
			boolean whilebreak = true;
			while (whilebreak) {
				ConsumerRecords<String, Protobuff> records = consumer.poll(100);
				int count = records.count();
				if (count == 0) {
					break;
				} else if (count < 100) {
					whilebreak = false;
				}
				for (ConsumerRecord<String, Protobuff> record : records)
					protobuffs.addProtobuff(record.value());
			}
			String protobuffsJson = gson.toJson(protobuffs);
			response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(protobuffsJson.getBytes()));
			} catch (Exception e) {
				logger.error("Error");
				response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST,
						Unpooled.wrappedBuffer(ERROR_RESPONSE.getBytes()));
			} finally {
				if(consumer != null) consumer.close();	
			}

		} else {
			response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST,
					Unpooled.wrappedBuffer(ERROR_RESPONSE.getBytes()));
		}

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

			ctx.write(response);
		}

	}
	
	private boolean isURIValid(String uri) {
		String removeParams = uri.indexOf("?") > 0  ? uri.substring(0, uri.lastIndexOf("?")) : uri;
		if(removeParams.endsWith(URL_MESSAGE) || removeParams.endsWith(URL_MESSAGE+"/")) return true;
		return false;
	}

}
