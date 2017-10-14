package tk.zhangh.springcloud.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ZhangHao on 2017/10/13.
 */
@Service
@Slf4j
public class HelloService {
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "helloFallback")
    public String helloService() {
        long start = System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE-HA/hello", String.class);
        long end = System.currentTimeMillis();
        log.info("Spend time:{}", end - start);
        return responseEntity.getBody();
    }

    public String helloFallback() {
        return "error";
    }
}
