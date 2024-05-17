package me.zhengjie.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.security.TokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin",  password = "123456", roles = {"admin"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class AuthorizationControllerTest {
    @Autowired
    private MockMvc mvc;
    private static final String URL = "/auth/login";

    @MockBean
    private OnlineUserService onlineUserService;

    @MockBean
    private TokenProvider tokenProvider;


    /**
     * 测试登录成功
     * @throws Exception
     */
    @Test
    public void testSuccessfulLogin() throws Exception {
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
    }

    /**
     * 测试登录失败：密码错误/未加密/加密错误
     * @throws Exception
     */
    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // 创建 AuthUserDto 对象
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");
        authUser.setPassword("invalid_password");

        // 执行登录方法，并断言返回状态码是否为 401
        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(authUser))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * 用户名或密码缺失
     */
    @Test
    public void testLoginWithMissingCredentials() throws Exception {
        // 创建 AuthUserDto 对象
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");

        // 执行登录方法，并断言返回状态码是否为 400
        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(authUser))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    /**
     * 获得用户信息
     */
    @Test
    public void testGetUserInfo() throws Exception {
        // 执行获取用户信息方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/auth/info"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * 获得验证码
     */
    @Test
    public void testGetCode() throws Exception {
        // 执行获取验证码方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/auth/code"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * 退出登录
     */
    @Test
    public void testLogout() throws Exception {
        // 执行退出登录方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}