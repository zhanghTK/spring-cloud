package tk.zhangh.springcloud.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * Created by ZhangHao on 2017/10/19.
 */
@EnableBinding(value = {Sink.class, SinkSender.class})
@Slf4j
public class SinkReceiver {
    @StreamListener(Sink.INPUT)
    public void receiver(Object payload) {
        log.info("Received:" + payload);
    }
}
