package tk.zhangh.springcloud.service;

import org.springframework.web.bind.annotation.*;
import tk.zhangh.springcloud.dto.User;

/**
 * Created by ZhangHao on 2017/10/17.
 */
@RequestMapping("/refactor")
public interface HelloService {

    @GetMapping(value = "/hello4")
    String hello(@RequestParam("name") String name);

    @GetMapping(value = "/hello5")
    User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age);

    @PostMapping(value = "/hello6")
    String hello(@RequestBody User user);
}
