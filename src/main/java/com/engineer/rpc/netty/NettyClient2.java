package com.engineer.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engineer.rpc.RPCClient;
import com.engineer.rpc.RPCHello;

public class NettyClient2 implements RPCClient {
	static Logger logger = LoggerFactory.getLogger(NettyClient2.class);
	
	private String host;  
    private int port;
    private int timeout = 30000; //thirty seconds
    
    private Bootstrap b = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    
	public NettyClient2(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public void start() {
        try {
            b.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                public void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            new NettyClientChannelHandler2(timeout));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            
            // Wait until the connection is closed.
            Channel clientChannel = f.channel();
			//clientChannel.closeFuture().sync();
            ChannelFuture closeFuture = clientChannel.closeFuture();
			closeFuture.addListener(ChannelFutureListener.CLOSE);
			
			LOCAL.set(clientChannel);
			//closeFuture.sync();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // Shut down the event loop to terminate all threads.
            //group.shutdownGracefully();
        }
	}
	
	public void close() throws IOException {
		group.shutdownGracefully();
	}
	
	
	
	public static void main(String[] args) {
		NettyClient2 client = new NettyClient2("localhost",8099);
		client.start();
		
		try {
//			final RPCHello proxy = NettyClientHelper.getProxy(client, RPCHello.class);
//			String sayHello = proxy.sayHello("Test");
//			System.out.println(sayHello);
			
			try {
				final RPCHello proxy = NettyClientHelper.getProxy(client, RPCHello.class);
			    Runnable target = new Runnable(){
	                @Override
	                public void run() {
	                    for (int i = 0; i < 5; i++) {
	                    	logger.info("Print Resut ------- {}", proxy.sayHello(Thread.currentThread().getName()+"-Morly"+i));
//	                        try {
//	                            TimeUnit.SECONDS.sleep(1);
//	                        } catch (InterruptedException e) {
//	                            e.printStackTrace();
//	                        }
	                    }
	                    logger.info("Print Resut ------- {}", proxy.sayHello("DONE"));
	                }};
	            Thread thread1 = new Thread(target, "Test1");
	            Thread thread2 = new Thread(target, "Test2");
	            Thread thread3 = new Thread(target, "Test3");
	            Thread thread4 = new Thread(target, "Test4");
	            Thread thread5 = new Thread(target, "Test5");
	            
	            thread1.start();
	            thread2.start();
	            thread3.start();
	            thread4.start();
	            thread5.start();
	            
	            thread1.join();
	            thread2.join();
	            thread3.join();
	            thread4.join();
	            thread5.join();
			} catch (Exception e) {
				// TODO: handle exception
				logger.debug("",e);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("",e);
		}
		
		//TimeUnit.SECONDS.sleep(1);
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//proxy.sayHello("Done agagin");
	}
	
	private static final ThreadLocal<Channel> LOCAL = new ThreadLocal<Channel>();
	
	@Override
	public <T> T send(T msg) throws Exception {
		Channel clientChannel = LOCAL.get();
	    if (clientChannel == null || !clientChannel.isActive()) {
	    	logger.info("create new channel=========");
	        ChannelFuture f = b.connect(host, port).sync();
	        clientChannel = f.channel();
	        clientChannel.closeFuture().addListener(ChannelFutureListener.CLOSE);
	        
	        LOCAL.set(clientChannel);
	    }
	    NettyClientChannelHandler2 handler = clientChannel.pipeline().get(NettyClientChannelHandler2.class);
		T send = handler.send(msg);
		
		return send;
	}

}
