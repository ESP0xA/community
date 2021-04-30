package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

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

        // 将私信列表中未读的消息设为已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }


    // 从私信列表中获取未读的消息列表，返回一个消息id列表
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                 // 如果当前用户是接收者且消息处于未读状态
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
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

    // 发送私信
    // 这是一个异步请求，所以加上 @ResponseBody
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {   // 页面表单传递两个参数，一个是发送目标用户名，一个是内容
        User target = userService.findUserByName(toName);

        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");   // 如果目标用户为空，返回一个Json
        }

        Message message = new Message();
        // 设置发信人和收信人 id
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        // 设置会话 id xxx_xxx
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        // 设置私信内容
        message.setContent(content);
        // 设置发信时间
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        // 如果没有报错则给页面返回一个状态码 0
        // 如果报错的话将来统一解决
        return CommunityUtil.getJSONString(0);
    }

}
