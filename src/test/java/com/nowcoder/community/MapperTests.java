package com.nowcoder.community;

import java.util.*;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
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


}
