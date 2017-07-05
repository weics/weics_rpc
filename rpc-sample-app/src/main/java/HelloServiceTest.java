import com.weics.rpc.client.RpcProxy;
import com.weics.rpc.simple.client.HelloService;
import com.weics.rpc.simple.client.Person;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by weics on 2017/7/3.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {

    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest1() {
        // 调用代理的create方法，代理HelloService接口
        HelloService helloService = rpcProxy.create(HelloService.class);

        // 调用代理的方法，执行invoke
        String result = helloService.hello("World");
        System.out.println("服务端返回结果：");
        System.out.println(result);
    }

    @Test
    public void helloTest2() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("Yong", "Huang"));
        System.out.println("服务端返回结果：");
        System.out.println(result);
    }

}
