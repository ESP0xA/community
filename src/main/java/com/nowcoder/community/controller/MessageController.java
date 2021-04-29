package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @RequestMapping(path="/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        // 先获取当前登录用户
        User user = hostHolder.getUser();

        // 初始化分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 查询会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());

        // 把信息封装到Map中
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String ,Object> map = new HashMap<>();
                // 会话内容
                map.put("conversation", message);
                // 会话中消息总数
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 未读消息数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                // 会话中对方头像(直接传入对方User对象)
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询总未读消息数量
        int lettersUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", lettersUnreadCount);

        return "/site/letter";
    }

    // 私信详情
    // 在请求中传入会话id
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 初始化分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 获取私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        // 信息封装到map
        List<Map<String ,Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String , Object> map = new HashMap<>();
                // 私信内容
                map.put("letter", message);
                // 私信发起人
                map.put("fromUser", userService.findUserById(message.getFromId()));

                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 查询私信对象
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    // 根据会话id获取会话对象
    private User getLetterTarget(String conversationId) {
        String[] id = conversationId.split("_");
        int id0 = Integer.parseInt(id[0]);
        int id1 = Integer.parseInt(id[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }
}
