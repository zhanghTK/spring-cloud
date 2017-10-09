package tk.zhangh.springcloud.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public String index() {
        String serviceId = registration.getServiceId();
        for (ServiceInstance instance : client.getInstances(serviceId)) {
            log.info("/hello, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        }
        return "hello world";
    }
}
