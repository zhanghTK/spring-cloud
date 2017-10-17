package tk.zhangh.springcloud.web;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请求合并器
 * Created by ZhangHao on 2017/10/16.
 */
public class UserCollapseCommand extends HystrixCollapser<List<User>, User, Long> {
    private UserService userService;
    private Long userId;

    public UserCollapseCommand(UserService userService, Long userId) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("userCollapseCommand"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
                        .withTimerDelayInMilliseconds(100)));  // 时间延迟属性
        this.userService = userService;
        this.userId = userId;
    }

    /**
     * 请求参数
     */
    @Override
    public Long getRequestArgument() {
        return userId;
    }

    /**
     * 创建合并请求命令
     *
     * @param collapsedRequests 延迟时间窗口中收集到的所有获取单个User的请求<ResponseType,RequestArgumentType>
     */
    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        List<Long> userIds = new ArrayList<>(collapsedRequests.size());
        userIds.addAll(collapsedRequests.stream()
                .map(CollapsedRequest::getArgument)
                .collect(Collectors.toList()));
        return new UserBatchCommand(userService, userIds);
    }

    /**
     * 批量请求命令实例被触发执行完后回调
     *
     * @param batchResponse     批量命令返回结果
     * @param collapsedRequests 被合并的请求
     */
    @Override
    protected void mapResponseToRequests(List<User> batchResponse, Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        int count = 0;
        for (CollapsedRequest<User, Long> collapsedRequest : collapsedRequests) {
            User user = batchResponse.get(count++);
            collapsedRequest.setResponse(user);
        }
    }


}
