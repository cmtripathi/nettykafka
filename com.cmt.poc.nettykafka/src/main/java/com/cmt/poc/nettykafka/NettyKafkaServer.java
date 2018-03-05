/**
 * 
 */
package com.cmt.poc.nettykafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

import com.cmt.poc.nettykafka.modal.Protobuff;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author c.tripathi
 *
 */
public class NettyKafkaServer {

	static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		Properties kafkaProperties = new Properties();
		kafkaProperties.load(NettyKafkaServer.class.getResourceAsStream("/producer.properties"));
		KafkaProducer<String, Protobuff> kafkaProducer = new KafkaProducer<String, Protobuff>(kafkaProperties);

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new NettyKafkaInitializer(kafkaProducer));
			Channel ch = b.bind(PORT).sync().channel();
			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
