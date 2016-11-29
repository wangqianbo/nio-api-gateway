package netty.rest.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.RandomAccessFile;

/**
 * Created by wangqianbo on 2016/11/21.
 */
public class MyHttpRequestHandler
        extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             Object request) throws Exception {
        System.out.println(request.getClass());
        if (request instanceof DefaultHttpContent) {
            DefaultHttpContent requestContent = (DefaultHttpContent) request;
            System.out.println("length = " + requestContent.content().readableBytes());
        } else if (request instanceof DefaultLastHttpContent) {
            DefaultLastHttpContent content = (DefaultLastHttpContent) request;
            System.out.println("length = " + content.content().readableBytes());

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
