package me.zhengjie.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.service.DictDetailService;
import me.zhengjie.utils.PageResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class DictDetailControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DictDetailService dictDetailService;

    private String token;

    @BeforeEach
    public void login() throws Exception {
        // 登录操作，获取 token
    }

    @AfterEach
    public void logout() throws Exception {
        // 退出登录操作
    }

    @Test
    public void testQueryDictDetail() throws Exception {
        // 执行查询字典详情方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dictDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetDictDetailMaps() throws Exception {
        // 执行查询多个字典详情方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dictDetail/map")
                        .param("dictName", "testDict1,testDict2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateDictDetail() throws Exception {
        // 创建新的字典详情对象
        DictDetail newDictDetail = new DictDetail();
        newDictDetail.setLabel("Test Label");
        newDictDetail.setValue("test_value");

        // 执行创建字典详情方法，并断言返回状态码是否为 201
        mvc.perform(MockMvcRequestBuilders.post("/api/dictDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(newDictDetail))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateDictDetail() throws Exception {
        // 创建更新字典详情对象
        DictDetail updateDictDetail = new DictDetail();
        updateDictDetail.setId(1L);
        updateDictDetail.setLabel("Updated Label");
        updateDictDetail.setValue("updated_value");

        // 执行更新字典详情方法，并断言返回状态码是否为 204
        mvc.perform(MockMvcRequestBuilders.put("/api/dictDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(updateDictDetail))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteDictDetail() throws Exception {
        // 执行删除字典详情方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/api/dictDetail/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
