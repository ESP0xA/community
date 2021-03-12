package com.nowcoder.community.util;

// 常量接口

// 将账号邮件激活状态写在接口里，便于复用
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登陆凭证超时时间（用户没有选择记住我）
     * 半天
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下的登陆凭证超时时间
     * 半年
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
