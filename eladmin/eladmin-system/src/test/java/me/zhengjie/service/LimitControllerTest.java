package me.zhengjie.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LimitControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testTestLimit() throws Exception {
        // 模拟多次访问接口
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/api/limit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn().getResponse();
            assertThat(response.getContentAsString()).isEqualTo(Integer.toString(i + 1));
        }

        // 测试限流，第 11 次访问应该返回 429
        mvc.perform(MockMvcRequestBuilders.get("/api/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().is(429));
    }
}
