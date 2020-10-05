package com.aki.nettydemo.testtcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


//SimpleChatServerInitializer 用来增加多个的处理类到 ChannelPipeline 上，
// 包括编码、解码、SimpleChatServerHandler 等
public class SimpleChatServerInitializer extends
        ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //LineBasedFrameDecoder：依次编译bytebuf中的可读字符，判断看是否有“\n”或者“\r\n”，如果有，就以此位置为结束位置，从可读索引到结束位置区间的字节就组成了一行。它是以换行符为结束标志的解码器，支持携带结束符或者不携带结束符两种解码方式，同时支持单行的最大长度。如果连续读取到最大长度后，仍然没有发现换行符，就会抛出异常，同时忽略掉之前读到的异常码流。
        //FixedLengthFrameDecoder:是固定长度解码器，它能按照指定的长度对消息进行自动解码，开发者不需要考虑TCP的粘包等问题。利用FixedLengthFrameDecoder解码，无论一次性接收到多少的数据，他都会按照构造函数中设置的长度进行解码；如果是半包消息，FixedLengthFrameDecoder会缓存半包消息并等待下一个包，到达后进行拼包，直到读取完整的包。
        //DelimiterBasedFrameDecoder：是自定义的分隔符解码，构造函数的第一个参数表示单个消息的最大长度，当达到该长度后仍然没有查到分隔符，就抛出TooLongFrameException异常，防止由于异常码流缺失分隔符导致的内存溢出。
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());//解码
        pipeline.addLast(new StringEncoder());//编码
        pipeline.addLast(new SimpleChatServerHandler());

        System.out.println("SimpleChatClient:"+ch.remoteAddress() +"连接上");
    }
}
