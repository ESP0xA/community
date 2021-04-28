package com.nowcoder.community.dao;

import java.util.*;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface DiscussPostMapper {

    // 首页显示所有人的帖子，所以这里的方法参数userId不是为了这里准备的
    // 将来会开发 用户个人主页 功能，在用户个人主页中会根据userId查看当前用户的所有帖子。
    // offset: 分页帖子的起始行号
    // limit: 分页帖子的总行数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 为了显示总页数，需要查询一共多少帖子
    // @Param 是为了给参数起一个别名；
    // @Param 另外一个重要目的是：SQL里需要用到动态的条件（比如这里的userId在首页不需要用到，而在个人主页用户所有帖子时需要用到）
    // 且这个动态的条件需要用到该参数，恰巧该方法只有一个参数且在<if>里使用，此时必须要加 @Param
    int selectDiscussPostRows(@Param("userId")int userId);

    // 插入新帖子
    int insertDiscussPost(DiscussPost discussPost);
    
    // 查询一个帖子
    DiscussPost selectDiscussPostById(int id);

    // 用户发表评论后（comment db）更新表（discuss-post db）中对应评论数
    int updateCommentCount(int id, int commentCount);
}
