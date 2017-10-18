package tk.zhangh.springcloud.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
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

    @GetMapping(value = "/hello1")
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }

    @GetMapping(value = "/hello2")
    public User hello(@RequestHeader String name, @RequestHeader Integer age) {
        return new User().setName(name).setAge(age);
    }

    @PostMapping(value = "/hello3")
    public String hello(@RequestBody User user) {
        return "Hello " + user.getName() + ", " + user.getAge();
    }

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class User {
        private String id;
        private String name;
        private Integer age;
    }
}
