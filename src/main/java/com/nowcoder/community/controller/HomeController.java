package com.nowcoder.community.controller;

import java.util.*;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;


    @RequestMapping(path = "/index", method = RequestMethod.GET)
    // 方法调用之前，SpringMVC会自动实例化Model和Page，并将Page注入给Model。（不需要手动addAttribute）
    // 所以在Thymeleaf中可以直接访问Page对象中的数据
    public String getIndexPage(Model model, Page page) {   // - model

        // 查询并设置总帖子数
        page.setRows(discussPostService.findDiscussPostRows(0));
        // 设置页面路径（供当前页面复用）
        page.setPath("/index");

        List<DiscussPost> list;
        list = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit());

        // 需要根据DiscussPost中的userId查询用户名，以便首页展现
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);                                      // 先把帖子放进map里
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);                                      // 再把User放进map里

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());   // 从redis中查询每个帖子的赞数量
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        // 把数据注入进model
        model.addAttribute("discussPosts", discussPosts);
        return "/index"; //.html   - view
    }

    //
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
