package tk.zhangh.springcloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.zhangh.springcloud.dto.User;

/**
 * Created by ZhangHao on 2017/10/17.
 */
@RestController
public class ConsumerController {
    @Autowired
    HelloService helloService;

    @Autowired
    RefactorHelloService refactorHelloService;

    @RequestMapping(value = "/feign-consumer", method = RequestMethod.GET)
    public String helloConsumer() {
        return helloService.hello();
    }

    @RequestMapping(value = "/feign-consumer2", method = RequestMethod.GET)
    public String helloConsumer2() {
        return helloService.hello() + "\n"
                + helloService.hello("zh") + "\n"
                + helloService.hello("zh", 24) + "\n"
                + helloService.hello(new HelloService.User().setName("zh").setAge(24));
    }

    @GetMapping("/feign-consumer3")
    public String helloConsumer3() {
        return refactorHelloService.hello("zh") + "\n"
                + refactorHelloService.hello("zh", 24) + "\n"
                + refactorHelloService.hello(new User().setName("zh").setAge(24));
    }
}
