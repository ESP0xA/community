package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityIndexApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String textA = "这里可以赌博，可以嫖娼，可以吸毒，可以开票，哈哈哈！一二三四五";
        textA = sensitiveFilter.filter(textA);
        System.out.println(textA);

        String textB = "这里可以☆赌☆☆博☆☆，可以嫖☆☆娼，可以☆☆吸☆☆☆☆☆毒，可以☆开☆☆▷◁☆☆票，哈哈哈！☆☆一☆☆二☆☆☆☆☆三☆☆☆☆";
        textB = sensitiveFilter.filter(textB);
        System.out.println(textB);
    }
}
