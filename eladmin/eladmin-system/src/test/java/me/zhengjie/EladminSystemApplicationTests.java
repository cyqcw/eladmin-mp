package me.zhengjie;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class EladminSystemApplicationTests {

    @Test
    public void contextLoads() {
      log.info("Test类执行了");
    }

    public static void main(String[] args) {
    }
}

