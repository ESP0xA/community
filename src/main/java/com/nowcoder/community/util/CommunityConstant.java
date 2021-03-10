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
}
