package tk.zhangh.springcloud.web;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by ZhangHao on 2017/10/13.
 */
@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
}
