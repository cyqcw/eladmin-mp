package me.zhengjie;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.security.TokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
@Slf4j
public class AuthorizationControllerTest {
    @MockBean
    private OnlineUserService onlineUserService;

    @MockBean
    private TokenProvider tokenProvider;

    @BeforeAll
    public static void setUp(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: {}", authentication);
        log.info("{}",authentication.getAuthorities());
        System.out.println("测试开始");
    }
    @Test
    public void testLogin() throws Exception {
        AuthUserDto authUserDto = new AuthUserDto();
        authUserDto.setUsername("test");
        authUserDto.setPassword("test");
        authUserDto.setCode("1234");
        authUserDto.setUuid("1234");
        System.out.println(JSON.toJSONString(authUserDto));
        log.info("token: {}", tokenProvider);
        log.info("onlineUserService: {}", onlineUserService);
    }
}