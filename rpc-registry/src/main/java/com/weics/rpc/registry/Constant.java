package com.weics.rpc.registry;

/**
 * 常量的表示
 * Created by weics on 2017/6/28.
 */
public class Constant {

    public static final int ZK_SESSION_TIMEOUT = 500;//zk的超时的时间

    public static final String ZK_REGISTRY_PATH = "/registry";//注册的节点

    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";//节点
}
