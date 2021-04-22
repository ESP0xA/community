package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

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

    /**
     *
     * @param code  编号
     * @param msg   提示信息
     * @param map   业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        // 将数据封装到JSONObject中
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONObject(int code) {
        return getJSONString(code, null, null);
    }

    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "os", map));
    }
}
