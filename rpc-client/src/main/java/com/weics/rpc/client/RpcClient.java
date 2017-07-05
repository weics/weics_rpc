package com.weics.rpc.client;

import com.weics.rpc.common.RpcDecoder;
import com.weics.rpc.common.RpcEncoder;
import com.weics.rpc.common.RpcReponse;
import com.weics.rpc.common.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 框架的RPC的客户端   用户发送RPC的请求
 * Created by weics on 2017/7/3.
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcReponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private int port;

    private RpcReponse reponse;

    private final Object obj = new Object();

    public RpcClient(String host,int port){
        this.host = host;
        this.port = port;
    }


    /**
     * 链接服务器，发送消息
     * @param request
     * @return
     */
    public RpcReponse send(RpcRequest request) throws Exception{

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //pipeline中添加编码，解码  业务处理的handle
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcReponse.class))
                                    .addLast(RpcClient.this);

                        }
                    }).option(ChannelOption.SO_KEEPALIVE,true);


            //链接服务器
            ChannelFuture future = bootstrap.connect(host,port).sync();

            //将request对象写入到outhandle处理后发出  即使用rpcencoder编码器
            future.channel().writeAndFlush(request).sync();

            //用线程等待的方式决定是否关闭链接
            //这个的意义是  先在这里阻塞 等待获取服务器的返回，若返回，被唤醒，从而关闭链接
            synchronized (obj){
                obj.wait();
            }
            if (reponse != null){
                future.channel().closeFuture().sync();
            }
            return reponse;

        } finally {
            group.shutdownGracefully();
        }


    }







    /**
     * 读取服务端的返回的数据
     * @param channelHandlerContext
     * @param rpcReponse
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, RpcReponse rpcReponse)
            throws Exception {

        this.reponse = rpcReponse;

        synchronized (obj){
            obj.notifyAll();
        }

    }


    /**
     * 异常的处理
     */


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }
}
