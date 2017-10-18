package tk.zhangh.springcloud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by ZhangHao on 2017/10/17.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private Integer age;
}
