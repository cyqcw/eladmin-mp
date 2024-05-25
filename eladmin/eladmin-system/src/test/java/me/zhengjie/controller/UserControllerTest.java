package me.zhengjie.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.domain.vo.UserPassVo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class UserControllerTest {


    @Autowired
    private MockMvc mvc;

    private String token;

    String json = "{\n" +
            "  \"id\": null,\n" +
            "  \"username\": \"${username}\",\n" +
            "  \"nickName\": \"${nickname}\",\n" +
            "  \"gender\": \"男\",\n" +
            "  \"email\": \"${email}\",\n" +
            "  \"enabled\": \"true\",\n" +
            "  \"roles\": [\n" +
            "    {\n" +
            "      \"id\": 2\n" +
            "    }\n" +
            "  ],\n" +
            "  \"jobs\": [\n" +
            "    {\n" +
            "      \"id\": 11\n" +
            "    }\n" +
            "  ],\n" +
            "  \"dept\": {\n" +
            "    \"id\": 7\n" +
            "  },\n" +
            "  \"phone\": \"${phone}\"\n" +
            "}";
    @BeforeEach
    public void login() throws Exception {
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


    @Test
    public void testCreateUserWithInvalidUsername() throws Exception {
        createUser("ad", "nick", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidNickName() throws Exception {
        createUser("admin1", "", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        createUser("admin1", "ni", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidPhone() throws Exception {
        createUser("admin1", "nick", "", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidUsernameAndPhone() throws Exception {
        createUser("admin1", "nick", "cyqcw", "");
    }

    @Test
    public void testCreateUserWithInvalidPhoneLength() throws Exception {
        createUser("admin1", "nick", "cyqcw1@foxmail.com", "1666666666");
    }

    @Test
    public void testCreateUserWithInvalidPhoneFormat() throws Exception {
        createUser("admin1", "nick", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidUsernameAndEmail() throws Exception {
        createUser("admin", "nick", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidNickNameAndEmail() throws Exception {
        createUser("admin1", "管理员", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidEmailAndPhone() throws Exception {
        createUser("admin1", "nick", "201507802@qq.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidPhoneFormatAndEmail() throws Exception {
        createUser("admin1", "nick", "cyqcw1@foxmail.com", "18888888888");
    }

    @Test
    public void testCreateUserWithInvalidUsernameAndNickName() throws Exception {
        createUser("ad", "", "cyqcw1@foxmail.com", "16666666666");
    }

    @Test
    public void testCreateUserWithInvalidUsernameAndEmail1() throws Exception {
        createUser("admin1", "nick", "", "16666666666");
    }
    private void createUser(String username, String nickname, String email, String phone) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);
        user.setUsername(username);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPhone(phone);

        mvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
//    @Test
//    public void testQueryUser() throws Exception {
//        // 执行查询用户方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.get("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testCreateUser() throws Exception {
//        // 创建新用户对象
//        AuthUserDto newUser = new AuthUserDto();
//        newUser.setUsername("newUser");
//        newUser.setPassword("password");
//
//        // 执行创建用户方法，并断言返回状态码是否为 201
//        mvc.perform(MockMvcRequestBuilders.post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(newUser))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//    }
//
//    @Test
//    public void testUpdateUser() throws Exception {
//        // 创建更新用户对象
//        AuthUserDto updateUser = new AuthUserDto();
//        updateUser.setUsername("updatedUser");
//        updateUser.setPassword("newPassword");
//
//        // 执行更新用户方法，并断言返回状态码是否为 204
//        mvc.perform(MockMvcRequestBuilders.put("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(updateUser))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//    }
//
//    @Test
//    public void testDeleteUser() throws Exception {
//        // 构造删除用户的 ID 集合
//        Set<Long> userIds = new HashSet<>();
//        userIds.add(1L);
//        userIds.add(2L);
//
//        // 执行删除用户方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.delete("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(userIds))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testUpdateUserPass() throws Exception {
//        // 创建修改密码对象
//        UserPassVo updateUserPass = new UserPassVo();
//        updateUserPass.setOldPass("oldPassword");
//        updateUserPass.setNewPass("newPassword");
//
//        // 执行修改密码方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.post("/api/users/updatePass")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(updateUserPass))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testResetPwd() throws Exception {
//        // 构造重置密码的 ID 集合
//        Set<Long> userIds = new HashSet<>();
//        userIds.add(1L);
//        userIds.add(2L);
//
//        // 执行重置密码方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.put("/api/users/resetPwd")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(userIds))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testUpdateUserAvatar() throws Exception {
//        // 创建模拟的头像文件
//        // MultipartFile avatar = new MockMultipartFile("avatar", "test.jpg", "image/jpeg", new byte[10]);
//
//        // 执行更新用户头像方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.post("/api/users/updateAvatar")
//                        // .file("avatar", avatar.getBytes())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testUpdateUserEmail() throws Exception {
//        // 创建修改邮箱对象
//        AuthUserDto updateUserEmail = new AuthUserDto();
//        updateUserEmail.setPassword("password");
//
//        // 执行修改邮箱方法，并断言返回状态码是否为 200
//        mvc.perform(MockMvcRequestBuilders.post("/api/users/updateEmail/code")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JSON.toJSONString(updateUserEmail))
//                        .header("Authorization", this.token)
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
}
