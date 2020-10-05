package com.aki.nettydemo.test1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class SimpleChatServer {

    private int port;

    public SimpleChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // 一个死循环,完成连接的接收
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 接收到连接之后,转交给这个worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 我们需要创建一个ServerBootstrap启动NIO服务
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 设置处理渠道
                    .channel(NioServerSocketChannel.class)
                    // 我们通过增加pipeline的方式给channel增加事务处理监听
                    .childHandler(new SimpleChatServerInitializer())
                    //初始化服务端可连接队列,指定了队列的大小128,如果未设置或所设置的值小于1,Java将使用默认值50
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            System.out.println("SimpleChatServer 启动了");
            // 绑定端口并启动接收客户端信息
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 一直等待循环接收信息直到socket关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 退出,释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("SimpleChatServer 关闭了");
        }
    }

    public static void main(String[] args) throws Exception {
        new SimpleChatServer(8080).run();
    }
}
