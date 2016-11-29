package netty.rest.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by wangqianbo on 2016/11/21.
 */
public class SendRequestHandler
        extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive!!");
        RandomAccessFile file = new RandomAccessFile("b.jpg", "r");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/chinaapi/users/4/files");
        request.headers().add("x-ehealth-meta-apikey", "4");
        request.headers().add("x-ehealth-meta-apikey-type", "USER_ID");
        request.headers().add("x-ehealth-meta-signature", "USER_ID");
        request.headers().add("x-ehealth-meta-guid", "1234");
        request.headers().add("Content-Type", "application/octet-stream");

        request.headers().set(HttpHeaderNames.CONNECTION,
                HttpHeaderValues.KEEP_ALIVE);
        ctx.write(request);
        ctx.write( new DefaultFileRegion(file.getChannel(), 0, file.length()));
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive !!!!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass());
        if(msg instanceof DefaultHttpResponse) {
            DefaultHttpResponse response = (DefaultHttpResponse)msg;
            System.out.println(response);
            System.out.println(response.status());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
