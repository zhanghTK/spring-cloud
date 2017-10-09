package tk.zhangh.springcloud.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ZhangHao on 2017/10/9.
 */
@RestController
public class HelloController {

    @RequestMapping("hello")
    public String index() {
        return "hello world";
    }
}
