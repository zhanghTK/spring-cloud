package tk.zhangh.springcloud;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.zhangh.springcloud.stream.SinkSender;

/**
 * Created by ZhangHao on 2017/10/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApplicationTest {
    // 注入绑定
    @Autowired
    private SinkSender sinkSender;

    // 注入消息通道
    @Autowired
    private MessageChannel input;

    @Test
    public void contextLoads() {
        sinkSender.output().send(MessageBuilder.withPayload("From SinkSender").build());
    }

    @Test
    public void test() {
        input.send(MessageBuilder.withPayload("From MessageChannel").build());
    }
}