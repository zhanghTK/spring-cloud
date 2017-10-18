package tk.zhangh.springcloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZhangHao on 2017/10/18.
 */
@Slf4j
public class AccessFilter extends ZuulFilter {

    /**
     * 过滤器类型，决定执行实际
     * pre：请求被路由之前执行
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器执行顺序
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 过滤器是否执行（执行条件）
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("send {} request to {}", request.getMethod(), request.getRequestURL().toString());
        String accessToken = request.getParameter("accessToken");
        if (accessToken == null) {
            log.warn("access token is empty");
            ctx.setSendZuulResponse(false);  /// 过滤该请求，不进行路由
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("no access-token");
        }
        log.info("access token ok");
        return null;
    }
}
