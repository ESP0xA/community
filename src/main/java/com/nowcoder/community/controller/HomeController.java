package com.nowcoder.community.controller;

import java.util.*;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model) {   // - model
        List<DiscussPost> list;
        list = discussPostService.findDiscussPost(0, 0, 10);

        // 需要根据DiscussPost中的userId查询用户名，以便首页展现
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);                                      // 先把帖子放进map里
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);                                      // 再把User放进map里
                discussPosts.add(map);
            }
        }

        // 把数据注入进model
        model.addAttribute("discussPosts", discussPosts);
        return "/index"; //.html   - view
    }
}
