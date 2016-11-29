package test;

import io.netty.channel.ChannelFuture;
import netty.rest.proxy.HttpFileServer;
import org.asynchttpclient.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class HttpClientTest {
    public void test() throws ExecutionException, InterruptedException {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        Future<Response> f = asyncHttpClient.prepareGet("http://www.baidu.com/").execute();
        Response r = f.get();
        System.out.println(r.getResponseBody());
    }

    public void test1() {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        CompletableFuture<Response> promise = asyncHttpClient
                .prepareGet("http://www.baidu.com/")
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> { /* Something wrong happened... */
                    return null;
                })
                .thenApply(resp -> { /*  Do something with the Response */
                    System.out.println(resp.getUri().getHost());
                    return resp;
                });
        CompletableFuture<Response> promise1 = asyncHttpClient
                .prepareGet("http://www.google.com/")
                .execute()
                .toCompletableFuture()
                .exceptionally(t -> { /* Something wrong happened... */
                    return null;
                })
                .thenApply(resp -> { /*  Do something with the Response */
                    System.out.println(resp.getUri().getHost());
                    return resp;
                });
        CompletableFuture<Object> promise2 = promise1.thenCombine(promise, (response, response2) -> {
            System.out.println("complete!!!");
            return null;
        });
        System.out.println("task start");
        promise2.join(); // wait for completion
    }

    public void test2() throws FileNotFoundException {
        String url = "http://182.48.117.175:8080/chinaapi/users/4/files?share=false";
        File file = new File("a.jpg");
        RandomAccessFile input =  new RandomAccessFile(file,"r");
        input.getChannel();
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                .setDisableUrlEncodingForBoundRequests(true)
                .setChunkedFileChunkSize(1024)
                .setHttpClientCodecMaxChunkSize(1)
                .setKeepAlive(true)
                .setTcpNoDelay(false)
                .build();

        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(config);
        CompletableFuture<Response> future = asyncHttpClient
                .preparePost(url)
                .addHeader("x-ehealth-meta-apikey", "4")
                .addHeader("x-ehealth-meta-apikey-type", "USER_ID")
                .addHeader("x-ehealth-meta-signature", "USER_ID")
                .addHeader("x-ehealth-meta-guid", "1234")
                .setBody(new File("a.jpg"))
                .execute()
                .toCompletableFuture()
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .thenApply(response -> {
                    if (response != null) {
                        System.out.println(response.getStatusCode());
                        System.out.println(response.getResponseBody(Charset.forName("UTF-8")));
                    }
                    return response;
                });
        System.out.println("start task");
        future.join();
    }

    @Test
    public void test3() {
        int port = 8887;
        final HttpFileServer endpoint = new HttpFileServer();
        ChannelFuture future = endpoint.start(
                new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }

}
