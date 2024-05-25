package me.zhengjie.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
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
public class DeptControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeptService deptService;

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
    public void testExportDept() throws Exception {
        // 执行导出部门数据方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dept/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testQueryDept() throws Exception {
        // 执行查询部门方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.get("/api/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetDeptSuperior() throws Exception {
        // 构造部门 ID 列表
        List<Long> ids = Collections.singletonList(1L);

        // 执行查询部门上级数据方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.post("/api/dept/superior")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(ids))
                        .param("exclude", "false")
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateDept() throws Exception {
        // 创建新部门对象
        Dept newDept = new Dept();
        newDept.setName("New Department");

        // 执行创建部门方法，并断言返回状态码是否为 201
        mvc.perform(MockMvcRequestBuilders.post("/api/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(newDept))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateDept() throws Exception {
        // 创建更新部门对象
        Dept updateDept = new Dept();
        updateDept.setId(1L);
        updateDept.setName("Updated Department");

        // 执行更新部门方法，并断言返回状态码是否为 204
        mvc.perform(MockMvcRequestBuilders.put("/api/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(updateDept))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteDept() throws Exception {
        // 构造删除部门的 ID 集合
        Set<Long> deptIds = Collections.singleton(1L);

        // 执行删除部门方法，并断言返回状态码是否为 200
        mvc.perform(MockMvcRequestBuilders.delete("/api/dept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(deptIds))
                        .header("Authorization", this.token)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
