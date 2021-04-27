package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // Entity: 评论的目标。分为帖子，评论，课程等等，这些都可以被评论。
    // 根据Entity查询评论
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    // 根据Entity查询评论数量
    int selectCountByEntity(int entityType, int entityId);

}
