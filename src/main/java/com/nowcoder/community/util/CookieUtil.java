package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 从HttpServletRequest对象中获取Cookie
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {

        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }

        Cookie[] cookies = request.getCookies();

        // 遍历cookies
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 找到了
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        // 没找到
        return null;
    }
}
