package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.ArrayList;

@Mapper
public interface MessageMapper {

    // 查询当前用户会话列表，针对每个会话只返回一条最新的私信（总的会话列表，和不同用户）
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的私信列表（和某用户的会话列表）
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //  查询未读私信数量（分为两种，一种是总的未读数量，另一种是和某用户的会话未读数量）
    // 根据传入的 conversationId 动态判断需要查询哪一种
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增私信
    int insertMessage(Message message);

    // 更改私信状态（已读，删除等）
    // 更改多个id的私信
    int updateStatus(List<Integer> ids, int status);
}
