package tk.zhangh.springcloud.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangHao on 2017/10/9.
 */
@RestController
@Slf4j
public class HelloController {

    @Autowired
    private DiscoveryClient client;

    @Autowired
    private Registration registration;

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String index() throws InterruptedException {
        String serviceId = registration.getServiceId();
        for (ServiceInstance instance : client.getInstances(serviceId)) {
            int sleepTime = new Random().nextInt(1000);
            log.info("sleepTime:{}", sleepTime);
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            log.info("/hello, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        }
        return "hello world";
    }
}
