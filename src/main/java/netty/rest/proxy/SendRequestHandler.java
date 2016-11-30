package netty.rest.proxy;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.ACCEPT;
import static io.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;

/**
 * Created by wangqianbo on 2016/11/21.
 */
public class SendRequestHandler
        extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive!!");
        RandomAccessFile file = new RandomAccessFile("a.jpg", "r");
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/chinaapi/users/4/files?share=false");
        request.headers().set("x-ehealth-meta-apikey", "4");
        request.headers().set("x-ehealth-meta-apikey-type", "USER_ID");
        request.headers().set("x-ehealth-meta-signature", "USER_ID");
        request.headers().set("x-ehealth-meta-guid", "1234");
        request.headers().set(CONTENT_TYPE, "application/octet-stream");
//        request.headers().set(CONTENT_LENGTH, file.length());
        request.headers().set(ACCEPT_ENCODING, "gzip, deflate");
        request.headers().set(TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED);
        request.headers().set(HttpHeaderNames.CONNECTION,
                HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HOST, "182.48.117.175");
        request.headers().set(ACCEPT, "*/*");
        request.headers().set(USER_AGENT, "Mozilla/5.0 ");
        Object msg = new ChunkedNioFile(file.getChannel(), 1024);
        ctx.write(request).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                System.out.println("send request complete!!!");
            }
        });
        ctx.write(msg, ctx.newProgressivePromise())//
                .addListener(future -> {
                    System.out.println("write complete!!!");
                });
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT, ctx.newProgressivePromise()).addListener(future -> {
            System.out.println("EMPTY_LAST_CONTENT!!!");
        });
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelWritabilityChanged !!!!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive !!!!");
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        System.out.println("flush !!!!");
        super.flush(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(msg);
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass());
        if (msg instanceof DefaultHttpResponse) {
            DefaultHttpResponse response = (DefaultHttpResponse) msg;
//            System.out.println(response);
        }
        System.out.println(msg);
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
