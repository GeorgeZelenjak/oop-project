package nl.tudelft.oopp.livechat;

import nl.tudelft.oopp.livechat.config.H2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {MainApplication.class, H2Config.class})
public class MainApplicationTest {

    @Test
    public void contextLoads() {
    }

    @Test
    public void failedTest(){
        fail();
    }
}