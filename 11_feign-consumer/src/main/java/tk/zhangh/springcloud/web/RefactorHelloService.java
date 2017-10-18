package tk.zhangh.springcloud.web;

import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by ZhangHao on 2017/10/17.
 */
@FeignClient(value = "hello-service-ha")
public interface RefactorHelloService extends tk.zhangh.springcloud.service.HelloService {
}
