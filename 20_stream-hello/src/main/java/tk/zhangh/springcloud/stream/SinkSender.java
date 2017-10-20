package tk.zhangh.springcloud.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;

/**
 * Created by ZhangHao on 2017/10/20.
 */
public interface SinkSender {

    @Output(Sink.INPUT)
    MessageChannel output();
}
