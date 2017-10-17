package tk.zhangh.springcloud.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by ZhangHao on 2017/10/14.
 */
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

    public User defaultUser(Long id, Throwable e) {
        e.printStackTrace();
        return new User();
    }

    // 异常传播：
    // 方法执行的异常除了 HystrixBadRequestException 之外，都会被 Hystrix 认为命令执行失败，触发降级逻辑
    // 可以使用 ignoreExceptions 忽略指定异常
    // 异常获取：
    // getExecutionException/参数


    // =============================================缓存使用，以下缓存为Hystrix缓存=======================================

    // 设置缓存，key为所有参数
    @HystrixCommand
    @CacheResult
    public User getUser1(Long id) {
        return new User();
    }

    // 设置缓存，自定义key，指定专门生成key的方法
    @HystrixCommand
    @CacheResult(cacheKeyMethod = "getUserByIdCacheKey")
    public User getUser2(Long id) {
        return new User();
    }

    // 缓存key生成方法
    private Long getUserByIdCacheKey(Long id) {
        return id;
    }

    // 缓存清理
    @HystrixCommand
    @CacheRemove(commandKey = "getUserById1")  // commandKey 必填，指明请求缓存的请求命令
    public void update(User user) {

    }

    //    ================================================Hystrix使用=======================================================
    // Hystrix 注解方式定义，同步执行
    @HystrixCommand(
            commandKey = "getUserById",
            groupKey = "UserGroup",
            threadPoolKey = "getUserByIdThread",
            fallbackMethod = "defaultUser",
            ignoreExceptions = RuntimeException.class
    )
    public User getUserById1(Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    // Hystrix 注解方式定义，异步执行
    @HystrixCommand
    public Future<User> getUserById2(Long id) {
        return new AsyncResult<User>() {
            @Override
            public User invoke() {
                return restTemplate.getForObject("http://USER-SERVICE-HA/users/{1}", User.class, id);
            }
        };
    }

    // 同步执行
    public User getUserByid3(Long id) throws Exception {
        return new UserCommand(restTemplate, id).execute();
    }

    // 异步执行
    public Future<User> getUserByid4(Long id) throws Exception {
        return new UserCommand(restTemplate, id).queue();
    }

    // 响应式执行，立即执行
    public void getUserById5(Long id) throws Exception {
        Observable<User> hotObservable = new UserCommand(restTemplate, id).observe();
    }

    // 响应式执行，只有当所有订阅者都订阅它之后才会执行
    public void getUserById6(Long id) throws Exception {
        Observable<User> coldObservable = new UserCommand(restTemplate, id).toObservable();
    }

    // 响应式执行，立即执行
    public void getUserById7(Long id) throws Exception {
        Observable hotObservable = new UserObservableCommand(restTemplate, id).observe();
    }

    // 响应式执行，只有当所有订阅者都订阅它之后才会执行
    public void getUserById8(Long id) throws Exception {
        Observable observable = new UserObservableCommand(restTemplate, id).toObservable();
    }

    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)
    public Observable<User> getUserById9(Long id) throws Exception {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        User user = restTemplate.getForObject("http://USER-SERVICE-HA/users/{1}", User.class, id);
                        subscriber.onNext(user);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable<User> getUserById10(Long id) throws Exception {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        User user = restTemplate.getForObject("http://USER-SERVICE-HA/users/{1}", User.class, id);
                        subscriber.onNext(user);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    //  ====================================================注解实现请求合并=================================================
    @HystrixCollapser(batchMethod = "findAll1",   // 指定批量请求的实现方法
            collapserProperties = // 合并请求器相关属性
                    {@HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})  // 合并时间窗设置为100毫秒
    public User find1(Long id) {
        return null;
    }

    @HystrixCommand
    public List<User> findAll1(List<Long> ids) {
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}", List.class, StringUtils.join(ids, ","));
    }

    //  =====================================================其他DEMO使用====================================================
    public User find(Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    public List<User> findAll(List<Long> ids) {
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}", List.class, StringUtils.join(ids, ","));
    }
}
