package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞

    /**
     * @param userId        点赞人
     * @param entityType    被点赞的实体类型
     * @param entityId      被点赞的实体Id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
/*        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 先判断用户对该实体有没有点过赞
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) {
            // 取消点赞，将userId从集合中删除
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }*/
        // 此处涉及到给某个实体点赞和给实体对应用户点赞数加一共两个操作，需要用到Redis事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId); // 此处如果根据entityId去数据库查询的话，会影响性能，不符合redis高性能特征，所以要求直接作为参数传入

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);  // 此时事务没有启动 ，只是放到了队列中

                operations.multi();

                // 以下两次操作为一个事务
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    // 另外还需要在页面上统计赞的数量
    // 查询实体被点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 显示某实体被某人赞的状态（有无点赞）
    // 返回整数类型，可以表示三种状态：赞，无赞无踩，踩
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);  // 返回一个Object
        return count == null ? 0 : count.intValue();
    }
}
