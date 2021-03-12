package com.nowcoder.community;

import java.util.*;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityIndexApplication.class)
public class MapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectPost() {
        // 查询10条帖子
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        // 查询帖子数量
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);

    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket;
        loginTicket = loginTicketMapper.selectByTicket("e67295fc18fd45638df912daabb430ed");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateLoginTicket() {
        loginTicketMapper.updateStatus("e67295fc18fd45638df912daabb430ed", 1);
    }

}
