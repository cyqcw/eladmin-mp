package me.zhengjie.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.domain.Dict;
import me.zhengjie.modules.system.service.DictService;
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
public class DictControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DictService dictService;

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
    public void testExportDict() throws Exception {
        // 执行导出字典数据方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dict/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testQueryAllDict() throws Exception {
        // 执行查询所有字典方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dict/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testQueryDict() throws Exception {
        // 执行查询字典方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateDict() throws Exception {
        // 创建新字典对象
        Dict newDict = new Dict();
        newDict.setName("Test Label");
        newDict.setDescription("test_value");

        // 执行创建字典方法，并断言返回状态码是否为 201
        mvc.perform(MockMvcRequestBuilders.post("/api/dict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(newDict))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateDict() throws Exception {
        // 创建更新字典对象
        Dict updateDict = new Dict();
        updateDict.setId(1L);
        updateDict.setName("Updated Label");
        updateDict.setDescription("updated_value");

        // 执行更新字典方法，并断言返回状态码是否为 204
        mvc.perform(MockMvcRequestBuilders.put("/api/dict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(updateDict))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteDict() throws Exception {
        // 构造删除字典的 ID 集合
        Set<Long> dictIds = Collections.singleton(1L);

        // 执行删除字典方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/api/dict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(dictIds))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
