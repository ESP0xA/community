package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.HashMap;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST) // 点赞需要post一些信息
    @ResponseBody   // 点赞需要用到异步请求，所以需要用到responseBody
    public String Like(int entityType, int entityId) { // 传入被点赞对象
        /*
        System.out.println(entityType);
        System.out.println(entityId);
        */
        User user = hostHolder.getUser();
        // 可以使用拦截器判断用户是否登录，将来还会用SpringSecurity对权限问题统一处理，在此先不做判断。

        // 点赞
        likeService.like(user.getId(), entityType, entityId);
        // 显示点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 返回的结果
        // 用hashmap将点赞数量和状态封装之后统一传给页面
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        //System.out.println("ok");
        // 以json格式返回给discuss.js的回调函数
        return CommunityUtil.getJSONString(0, null, map);   // 正确返回0，提示信息null
    }
}
