package me.zhengjie.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.security.TokenProvider;
import me.zhengjie.utils.RsaUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin",  password = "123456", roles = {"admin"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class AuthorizationControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * RSA 公钥
     */
    private final String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ==";

    // 测试用例1：用户名和密码均有效，登录成功
    @Test
    public void testLoginSuccess() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456"));
        performLoginAndAssert(authUser, HttpStatus.OK.value(), null);
    }

    // 测试用例2：用户名为空，登录失败
    @Test
    public void testLoginFailWithEmptyUsername() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername(""); // 空用户名
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "username: 用户名长度不正确");
    }

    // 测试用例3：用户名长度不足，登录失败
    @Test
    public void testLoginFailWithShortUsername() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cy"); // 用户名太短
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "username: 用户名长度不正确");
    }

    // 测试用例4：用户名长度过长，登录失败
    @Test
    public void testLoginFailWithLongUsername() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcwcyqcwcyqcwcyqcwcyqcwcyqcw"); // 用户名太长
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "username: 用户名长度不正确");
    }

    // 测试用例5：密码为空，登录失败
    @Test
    public void testLoginFailWithEmptyPassword() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"")); // 密码为空
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "password: must not be blank");
    }

    // 测试用例6：密码长度不足，登录失败
    @Test
    public void testLoginFailWithShortPassword() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"12345")); // 密码太短
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "密码格式不正确");
    }

    // 测试用例7：密码长度过长，登录失败
    @Test
    public void testLoginFailWithLongPassword() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"1234567891234567891")); // 密码太长
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "密码格式不正确");
    }

    // 测试用例8：密码包含非法字符，登录失败
    @Test
    public void testLoginFailWithInvalidCharInPassword() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456.")); // 密码含非法字符
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "密码格式不正确");
    }

    /**
     * 边界值分析
     */
    // 测试用例1：用户名长度为3，密码长度为12，正常登录成功
    @Test
    public void testLoginSuccessWithMinBoundaryValues() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例2：用户名长度为3，密码长度为19，登录失败，密码长度超出范围
    @Test
    public void testLoginFailWithMaxPasswordLength() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyq");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例3：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testLoginSuccessWithMaxPasswordLengthMinusOne() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqc");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例4：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testLoginSuccessWithMaxPasswordLengthPlusOne() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcwcyqcwcyqcwcyqc");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例5：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testLoginSuccessWithMaxBoundaryValues() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcwcyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456_cyqcw"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例6：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testBoundaryLoginSuccessWithInvalidCharacterInPassword1() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例7：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testBoundaryLoginSuccessWithInvalidCharacterInPassword2() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"1234567"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例8：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testBoundaryLoginSuccessWithInvalidCharacterInPassword3() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"12345678912345678"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 测试用例9：用户名长度为3，密码长度为18，登录成功
    @Test
    public void testLoginSuccessWithInvalidCharacterInPassword() throws Exception {
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("cyqcwcyqcwcyqcw");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456789123456789"));
        performLoginAndAssert(authUser, HttpStatus.BAD_REQUEST.value(), "Bad credentials");
    }

    // 辅助方法，用于执行登录请求并断言
    private void performLoginAndAssert(AuthUserDto authUser, int expectedStatus, String expectedMessage) throws Exception {
        String response = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(authUser)))
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
                .andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = JSON.parseObject(response);
        assertEquals(expectedMessage, jsonObject.getString("message"));
        log.info("Test response: {}", jsonObject);
    }

    /**
     * 获得用户信息
     */
    @Test
    public void testGetUserInfo() throws Exception {
        // 创建 AuthUserDto 对象
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456"));

        // 执行登录方法，并断言返回状态码是否为 200
        String response = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(authUser))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = JSON.parseObject(response);
        log.info("jsonObject: {}", jsonObject);

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
     * 退出登录: 先登录，再退出
     */
    @Test
    public void testLogout() throws Exception {
        // 创建 AuthUserDto 对象
        AuthUserDto authUser = new AuthUserDto();
        authUser.setUsername("admin");
        authUser.setPassword(RsaUtils.encryptByPublicKey(publicKey,"123456"));

        // 执行登录方法，并断言返回状态码是否为 200
        String response = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(authUser))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = JSON.parseObject(response);
        log.info("jsonObject: {}", jsonObject);
        String token = jsonObject.get("token").toString();

        // 执行退出登录方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/auth/logout")
                        .header("Authorization", token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}