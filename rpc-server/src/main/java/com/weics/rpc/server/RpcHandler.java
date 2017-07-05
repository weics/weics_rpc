package com.weics.rpc.server;

import com.weics.rpc.common.RpcReponse;
import com.weics.rpc.common.RpcRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * 处理具体的业务调用
 * 通过构造时候传入的"业务接口及实现"handlerMap 来调用客户端所请求的业务方法
 * 并且将业务方法返回值封装成reponse对象写入下一个handle （即编码handler——RpcEncoder）
 * Created by weics on 2017/7/3.
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RpcHandler.class);

    private final Map<String,Object> handlerMap;

    public RpcHandler(Map<String,Object> handlerMap){
        this.handlerMap = handlerMap;
    }


    /**
     * 接受消息，处理消息，返回结果
     * @param channelHandlerContext
     * @param request
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {

        RpcReponse reponse = new RpcReponse();

        reponse.setRequestId(request.getRequestId());

        try {
            Object handle = handle(request);

            reponse.setResult(handle);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            reponse.setError(throwable);
        }

        //写入 outbundle（即RpcEncoder）进行下一步处理（即编码）后发送到channel中给客户端

        channelHandlerContext.writeAndFlush(reponse).addListener(ChannelFutureListener.CLOSE);


    }



    /**
     * 根据request来处理具体的业务调用
     * 调用时通过反射的方式来完成的
     */

    private Object handle(RpcRequest request) throws Throwable{
        String className = request.getClassName();

        //拿到类的实现类
        Object serviceBean = handlerMap.get(className);

        //拿到要调用的方法名，参数类型，参数值
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //拿到接口类
        Class<?> forName = Class.forName(className);

        //调用实现类对象的指定房扥啊并且返回结果
        Method method = forName.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean,parameters);

    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

}
