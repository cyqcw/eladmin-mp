package me.zhengjie.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.service.dto.OnlineUserDto;
import me.zhengjie.utils.RedisUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WithMockUser(username = "admin",  password = "123456", roles = {"admin"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class OnlineControllerTest {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MockMvc mvc;

    private String token;

    @BeforeEach
    public void login() throws Exception {
        log.info("login方法test开始执行了");
        // 创建 AuthUserDto 对象
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");
        authUser.setPassword("Lzm8uU1Mr5l9eksj4nCaY4VvhxnoAQaM26rSFk5MFJ1QV2iu9BzLz7gdbynoCi+XFWxrdJElp6Hh43Vml3n70A==");

        // 执行登录方法，并断言返回状态码是否为 200
        String response = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(authUser))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = JSON.parseObject(response);
        log.info("jsonObject: {}", jsonObject);
        this.token = jsonObject.get("token").toString();
    }

    @AfterEach
    public void logout() throws Exception {
        // 执行退出登录方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/auth/logout")
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * 测试查询在线用户
     * @throws Exception
     */
    @Test
    public void testQueryOnlineUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/auth/online")
                .contentType("application/json")
                .header("Authorization", this.token)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .param("username", "admin")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * 测试导出数据接口
     */

    @Test
    public void testExportOnlineUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/auth/online/download")
                        .contentType("application/json")
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .param("username", "admin"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * 测试踢出用户接口
     * @throws Exception
     */
    @Test
    public void testDeleteOnlineUser() throws Exception {

        List<String> keys = redisUtils.scan("online-token:*");
        OnlineUserDto onlineUserDto =(OnlineUserDto) redisUtils.get(keys.get(keys.size()-1));
        String delToken = onlineUserDto.getKey();
        Set<String> delTokens = new HashSet<>();
        delTokens.add(delToken);
        // 构造请求体，模拟传入的 token 集合
        String requestBody = JSON.toJSONString(delTokens);

        mvc.perform(MockMvcRequestBuilders.delete("/auth/online")
                        .contentType("application/json")
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
