package tk.zhangh.springcloud.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by ZhangHao on 2017/10/13.
 */
@RestController
public class ConsumerController {

    private static final String URL = "http://localhost:8080/user?id={1}";
    private static final String ID = "1";

    private RestTemplate restTemplate = new RestTemplate();

    //    =================================================GET==========================================================
    @GetMapping(value = "get1")
    public String getEntityString() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL, String.class, ID);
        return responseEntity.getBody();
    }

    @GetMapping(value = "get2")
    public User getEntityUser() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(URL, User.class, ID);
        return responseEntity.getBody();
    }

    @GetMapping(value = "get3")
    public String getObjectString() {
        return restTemplate.getForObject(URL, String.class, ID);
    }

    @GetMapping(value = "get4")
    public User getObjectUser() {
        return restTemplate.getForObject(URL, User.class, ID);
    }

    //    ================================================POST==========================================================
    @GetMapping(value = "post1")
    public String postEntity() {
        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(URL, new User().setId("1").setName("name"), String.class);
        return responseEntity.getBody();
    }

    @GetMapping(value = "post2")
    public User postEntityUser() {
        ResponseEntity<User> responseEntity = restTemplate.
                postForEntity(URL, new User().setId("1").setName("name"), User.class, ID);
        return responseEntity.getBody();
    }

    @GetMapping(value = "post3")
    public String postObjectString() {
        return restTemplate.postForObject(URL, new User().setId("1").setName("name"), String.class, ID);
    }

    @GetMapping(value = "post4")
    public User postObjectUser() {
        return restTemplate.postForObject(URL, new User().setId("1").setName("name"), User.class, ID);
    }

    @GetMapping(value = "post5")
    public String postForLocation() {
        URI uri = restTemplate.postForLocation(URL, new User().setId("1").setName("name"), ID);
        return "post success";
    }

    //    =================================================PUT==========================================================
    @GetMapping(value = "put")
    public String put() {
        restTemplate.put(URL, new User().setId("1").setName("name"), ID);
        return "put success";
    }

    //    =================================================DELETE=======================================================
    @GetMapping(value = "delete")
    public String delete() {
        restTemplate.delete(URL, ID);
        return "delete success";
    }
}
