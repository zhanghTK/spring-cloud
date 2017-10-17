package tk.zhangh.springcloud.web;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ZhangHao on 2017/10/14.
 */
public class UserCommand extends HystrixCommand<User> {

    private static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("CommandName");
    @Autowired
    private RestTemplate restTemplate;
    private Long id;

    // Hystrix 默认（未指定线程池）让相同组名的命令使用同一个线程池
    public UserCommand(RestTemplate restTemplate, Long id) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"))  // 命令组
                .andCommandKey(HystrixCommandKey.Factory.asKey("CommandName"))  // 命令
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolKey"))  // 线程池
        );  // 命令
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * 清除缓存
     */
    public static void flushCache(Long id) {
        HystrixRequestCache.getInstance(
                COMMAND_KEY,  // command name
                HystrixConcurrencyStrategyDefault.getInstance()  // 默认并发策略
        ).clear(String.valueOf(id));
    }

    @Override
    protected User run() throws Exception {
        // 除了 HystrixBadRequestException 之外，都会被 Hystrix 认为命令执行失败，触发降级逻辑
        return restTemplate.getForObject("http://USER-SERVICE-HA/users/{1}", User.class, id);
    }

    @Override
    protected User getFallback() {
        Throwable executionException = getExecutionException();  // 异常获取
        executionException.printStackTrace();
        return new User();
    }

    /**
     * 缓存请求
     */
    @Override
    protected String getCacheKey() {
        // 根据 ID 置入缓存
        return String.valueOf(id);
    }
}
