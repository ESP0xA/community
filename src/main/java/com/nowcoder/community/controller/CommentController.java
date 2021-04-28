package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    // 处理添加评论的请求
    // 发完帖子后重定向到该帖子，所以需要帖子id，在这里传进来
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {    // 页面传入多组数据，如comment内容，entityType, entityId，另外一些数据在这里统一封装

        // 当用户未登录时，会引发异常，后面做统一异常处理；或者权限认证也可以规避。
        comment.setUserId(hostHolder.getUser().getId());
        // 设置当前评论是有效的
        comment.setStatus(0);
        // 设置评论当前时间
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        // 重定向到帖子详情页
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
