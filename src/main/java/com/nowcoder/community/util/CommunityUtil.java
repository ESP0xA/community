package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    // 生成一个随机字符串

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");    // 不需要 分割线-
    }

    // MD5加密

    /**
     *
     * @param key 原始口令字符串加盐
     * @return md5值
     */
    public static String md5(String key) {

        // 当key为null或key为空字符串或key为空格，都认为是空
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 使用spring的创建md5 hexString方法
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}
