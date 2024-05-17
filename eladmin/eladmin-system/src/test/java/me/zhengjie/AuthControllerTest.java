package me.zhengjie;

import me.zhengjie.modules.system.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author：HeGu
 * @date：2024/5/10
 */
@WithMockUser(username = "admin", authorities = {"user:list"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @BeforeAll
    public static void setUp(){
        System.out.println("测试开始");
    }
    @AfterAll
    public static void tearDown(){
        System.out.println("测试结束");
    }

    @Test
    public void test() {
        System.out.println(userService.findById(1L));
    }
}
