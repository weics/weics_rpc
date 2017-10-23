package com.weics.rpc.simple.client;

/**
 * Created by weics on 2017/7/3.
 */
public interface  HelloService {

    String hello(String name);

    String hello(Person person);

}
