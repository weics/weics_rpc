package com.weics.rpc.client;

import com.weics.rpc.common.RpcReponse;
import com.weics.rpc.common.RpcRequest;
import com.weics.rpc.registry.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by weics on 2017/7/3.
 */
public class RpcProxy {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        System.out.println("spring RpcProxy注入成功serverAddress");
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        System.out.println("spring RpcProxy注入成功ServiceDiscovery");
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建代理
     *
     * @param interfaceClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass }, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
                        //创建RpcRequest，封装被代理类的属性
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        //拿到声明这个方法的业务接口名称
                        request.setClassName(method.getDeclaringClass()
                                .getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        //查找服务
                        if (serviceDiscovery != null) {
                            System.out.println("开始进入发现服务列表的过程");
                            serverAddress = serviceDiscovery.discover();
                        }
                        //随机获取服务的地址
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        System.out.println("获取的host地址是====="+host);
                        int port = Integer.parseInt(array[1]);

                        System.out.println("获取的port地址是====="+port);
                        //创建Netty实现的RpcClient，链接服务端
                        RpcClient client = new RpcClient(host, port);
                        //通过netty向服务端发送请求
                        RpcReponse response = client.send(request);
                        //返回信息
                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}
