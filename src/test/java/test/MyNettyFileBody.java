package test;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.netty.request.body.NettyFileBody;

import java.io.File;

/**
 * Created by wangqianbo on 2016/11/28.
 */
public class MyNettyFileBody extends NettyFileBody{
    public MyNettyFileBody(File file, AsyncHttpClientConfig config) {
        super(file, config);
    }

    public MyNettyFileBody(File file, long offset, long length, AsyncHttpClientConfig config) {
        super(file, offset, length, config);
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }
}
