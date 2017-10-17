package tk.zhangh.springcloud.web;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by ZhangHao on 2017/10/16.
 */
public class UserObservableCommand extends HystrixObservableCommand {
    private RestTemplate restTemplate;
    private Long id;

    public UserObservableCommand(RestTemplate restTemplate, Long id) {
        super(HystrixCommandGroupKey.Factory.asKey(""));
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected Observable construct() {
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

    @Override
    protected Observable resumeWithFallback() {
        return null; // TODO
    }
}
