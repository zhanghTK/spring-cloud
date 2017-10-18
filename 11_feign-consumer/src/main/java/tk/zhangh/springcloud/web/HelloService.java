package tk.zhangh.springcloud.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ZhangHao on 2017/10/17.
 */
@FeignClient(value = "hello-service-ha", fallback = HelloServiceFallback.class, configuration = FullLogConfiguration.class)
public interface HelloService {

    @GetMapping("/hello")
    String hello();

    @GetMapping(value = "/hello1")
    String hello(@RequestParam("name") String name);

    @GetMapping(value = "/hello2")
    User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age);

    @PostMapping(value = "/hello3")
    String hello(@RequestBody User user);

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    class User {
        private String id;
        private String name;
        private Integer age;
    }
}
