package tk.zhangh.springcloud.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ZhangHao on 2017/10/13.
 */
@RestController
public class UserController {

    @Autowired
    private HttpServletRequest request;

    private static String getBody(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            ServletInputStream ex = request.getInputStream();
            if (ex != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(ex));
                char[] charBuffer = new char[128];

                int bytesRead1;
                while ((bytesRead1 = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead1);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    @PostMapping(value = "/user")
    public User post(@RequestParam(value = "id", required = false) String id,
                     @RequestParam(value = "name", required = false) String name) {
        System.out.println(getBody(request));
        return new User().setId(id).setName(name);
    }

    @DeleteMapping(value = "/user")
    public void delete(@RequestParam(value = "id", required = false) String id) {
        System.out.println("put:" + id);
    }

    @PutMapping(value = "/user")
    public void put(@RequestParam(value = "id", required = false) String id,
                    @RequestParam(value = "name", required = false) String name) {
        System.out.println("put:" + id + "," + name);
    }

    @GetMapping(value = "/user")
    public User get(@RequestParam(value = "id", required = false) String id) {
        return new User().setId(id).setName("name");
    }
}
