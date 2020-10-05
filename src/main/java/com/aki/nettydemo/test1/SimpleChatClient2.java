package com.aki.nettydemo.test1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//客户端1 和客户端2一模一样就是为了启动两个客户端
public class SimpleChatClient2 {
    public static void main(String[] args) throws Exception{
        new SimpleChatClient2("localhost", 8080).run();
    }

    private final String host;
    private final int port;

    public SimpleChatClient2(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception{
        // 处理线程组  workergroup中取出一个管道channel来建立连接
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap  = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleChatClientInitializer());
            // 绑定 ip 端口 创建连接 调用sync()方法会阻塞直到服务器完成绑定  然后服务器再获取通道channel
            Channel channel = bootstrap.connect(host, port).sync().channel();
            //定义向服务器发送的内容  system.in  控制台输入   in.readLine() 获取值 每次读一行。换句话说，用户输入一行内容，然后回车，这些内容一次性读取进来。
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                //writeAndFlush()方法分为两步, 先 write 再 flush
                channel.writeAndFlush(in.readLine() + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 退出，释放线程池资源
            group.shutdownGracefully();
        }

    }
}
