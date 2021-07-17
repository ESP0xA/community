package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";    // 常量，将redis使用中key:value中的 : 替换掉

    private static final String PREFIX_ENTITY_LIKE = "like:entity";     // 实体赞的前缀
    private static final String PREFIX_USER_LIKE = "like:user";

    // 针对某个实体的赞
    // 返回某个实体的赞的key，前缀拼接type和id

    // 考虑如何表示一个实体赞，可以是一个整数，那么点赞就加一。但是这样无法装载点赞人的信息，那么可以考虑将点赞人的userId放入到集合中
    // 如果要统计点赞总数可以直接取集合大小
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 针对某个用户的赞
    // like:user:userId -> set(userId)
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
