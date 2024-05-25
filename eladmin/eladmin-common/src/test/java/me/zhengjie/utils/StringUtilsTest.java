package me.zhengjie.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static me.zhengjie.utils.StringUtils.getIp;
import static me.zhengjie.utils.StringUtils.getWeekDay;
import static me.zhengjie.utils.StringUtils.toCamelCase;
import static me.zhengjie.utils.StringUtils.toCapitalizeCamelCase;
import static me.zhengjie.utils.StringUtils.toUnderScoreCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {

    @Test
    public void testToCamelCase() {
        assertNull(StringUtils.toCamelCase(null));
        assertEquals("helloWorld", StringUtils.toCamelCase("hello_world"));
    }

    @Test
    public void testToCapitalizeCamelCase() {
        assertNull(StringUtils.toCapitalizeCamelCase(null));
        assertEquals("HelloWorld", toCapitalizeCamelCase("hello_world"));
    }

    @Test
    public void testToUnderScoreCase() {
        assertNull(StringUtils.toUnderScoreCase(null));
        assertEquals("hello_world", toUnderScoreCase("helloWorld"));
        assertEquals("\u0000\u0000", toUnderScoreCase("\u0000\u0000"));
        assertEquals("\u0000_a", toUnderScoreCase("\u0000A"));
    }

    @Test
    public void testGetWeekDay() {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
        assertEquals(simpleDateformat.format(new Date()), getWeekDay());
    }

    @Test
    public void testGetIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");
        assertEquals("192.168.1.1", getIp(request));
        assertEquals("10.125.235.81", getIp(new MockHttpServletRequest()));
    }

    @Test
    public void testGetCityInfo() {
        String ip = "59.77.7.17";
        String expectedCityInfo = "中国福建漳州";
        assertEquals(expectedCityInfo, StringUtils.getCityInfo(ip));
    }

    @Test
    public void testGetBrowser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537");
        assertEquals("Chrome 58.0.3029.110", StringUtils.getBrowser(request));
    }

    @Test
    public void testGetLocalIp() {
        String ip = StringUtils.getLocalIp();
        // 检查IP地址的格式
        assertTrue(Pattern.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", ip));
    }

    @Test
    public void testGetAllFields() {
        // 创建一个具有已知字段的类
        class TestClass {
            private int field1;
            public String field2;
        }

        List<Field> fields = StringUtils.getAllFields(TestClass.class, new ArrayList<>());
        // 检查返回的字段列表是否包含已知字段
        assertTrue(fields.stream().anyMatch(field -> field.getName().equals("field1")));
        assertTrue(fields.stream().anyMatch(field -> field.getName().equals("field2")));
    }
}
