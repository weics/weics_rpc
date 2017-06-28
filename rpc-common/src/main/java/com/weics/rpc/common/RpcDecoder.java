package com.weics.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * RPC的解码器
 * Created by weics on 2017/6/28.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;


    //构造函数传入向发序列化的class
    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }



    @Override
    public final void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4){
            return ;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0){
            channelHandlerContext.close();
        }

        if (byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
        }

        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);

        Object obj = SerializationUtil.deserialize(bytes, genericClass);
        list.add(obj);

    }
}
