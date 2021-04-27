package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody                                                       // 返回字符串而非网页
    public String addDiscussPost(String title, String content) {

        // 先获取User信息(检查登陆状态)
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录喔~");    // 403状态码表示没有权限
        }

        // 构造DiscussPost实体
        // 其中主键id自动生成，type和status以及comment_count和score都默认为0             // 为什么会存在默认值？？？
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 如果报错，将来会有统一处理 --> future
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    // 根据id查询帖子，一般会将id拼到路径中
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // 查询对应User
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 查询评论分页信息

        // 初始化page
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());        // 可以在comment表中统计，也可以从DiscussPost表中直接查询，显然后者更方便

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 查询评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());

        // 查询到的Comment列表中每个Comment包含UserId，我们需要根据UserId获取User，以便显示更多User信息
        // 用Map对所要展现的数据做一个统一的封装
        // 评论VO列表（ViewObject）
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String ,Object> commentVo = new HashMap<>();
                // 添加评论到评论VO
                commentVo.put("comment", comment);
                // 添加评论发起人到评论VO
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 对每条评论查询回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE); // 查所有回复，不分页
                // 回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String ,Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 回复发起人
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                // 添加回复VO到评论VO
                commentVo.put("replys", replyVoList);
                // 添加回复数量到评论VO
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                // 最后将当前评论VO加入评论VO列表
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
