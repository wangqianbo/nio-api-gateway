package netty.rest.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by wangqianbo on 2016/11/21.
 */
public class MyHttpRequestHandler
        extends SimpleChannelInboundHandler<Object> {
    HashMap<Channel, RandomAccessFile> files = new HashMap<>();
    private volatile  int count = 1;
    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             Object request) throws Exception {
        System.out.println(request.getClass());
        if (request instanceof DefaultHttpRequest) {
            DefaultHttpRequest defaultHttpRequest = (DefaultHttpRequest) request;
            System.out.println(defaultHttpRequest);
            count++;
            System.out.println("count = " + count);
            System.out.println("ctx.channel() = " + ctx.channel().hashCode());
            RandomAccessFile file = new RandomAccessFile( ctx.channel().hashCode()+ ".jpg", "rw");
            files.put(ctx.channel(), file);
        }
        if (request instanceof DefaultHttpContent) {
            DefaultHttpContent requestContent = (DefaultHttpContent) request;
            ByteBuf content = requestContent.content();
            RandomAccessFile file = files.get(ctx.channel());
            content.readBytes(file.getChannel(), file.length(), content.readableBytes());
        } else if (request instanceof DefaultLastHttpContent) {
            RandomAccessFile file = files.get(ctx.channel());
            file.close();
            DefaultLastHttpContent content = (DefaultLastHttpContent) request;

        }
        RandomAccessFile file = new RandomAccessFile("a.jpg", "r");
        HttpResponse response = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(
                HttpHeaderNames.CONTENT_TYPE,
                "text/plain; charset=UTF-8");
        boolean keepAlive = true;
        response.headers().set(
                HttpHeaderNames.CONTENT_LENGTH, 0);

        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
//        ctx.write(new ChunkedNioFile(file.getChannel()));
        ChannelFuture future = ctx.writeAndFlush(
                LastHttpContent.EMPTY_LAST_CONTENT);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
