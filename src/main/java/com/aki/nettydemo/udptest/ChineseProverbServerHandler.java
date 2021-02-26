package com.aki.nettydemo.udptest;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class ChineseProverbServerHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {
    private static final String[] DICTIONARY = {"小葱拌豆腐，一穷二白", "只要功夫深，铁棒磨成针", "山中无老虎，猴子称霸王"};

    private String nextQuote() {
        //线程安全岁基类，避免多线程环境发生错误
        int quote = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quote];
    }

    //接收Netty封装的DatagramPacket对象，然后构造响应消息
    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) {
        //利用ByteBuf的toString()方法获取请求消息
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        if ("谚语字典查询?".equals(req)) {
            //创建新的DatagramPacket对象，传入返回消息和目的地址（IP和端口）
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(
                    "谚语查询结果：" + nextQuote(), CharsetUtil.UTF_8), packet.sender()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
