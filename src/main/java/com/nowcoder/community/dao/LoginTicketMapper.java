package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

// Mapper接口的实现方式除了在resource - mapper中实现外，还可以在接口里使用注解的方式去声明方法对应的SQL。

@Mapper
public interface LoginTicketMapper {

    // 插入新的ticket
    @Insert({
            // 可以将一个SQL字符串拆分成多个
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")   // 设置自增长主键，将主键值注入给bean的id属性
    int insertLoginTicket(LoginTicket loginTicket);


    // 查询是以Ticket为条件的查询
    // 服务器生成一个Ticket字符串，发送给浏览器，保存在cookies中；那么，浏览器再次访问服务器的时候，会返回Ticket,服务器再根据这个区查询
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 修改Ticket状态
    @Update({
            "update login_ticket set status=#{status} ",
            "where ticket=#{ticket}"
    })
    /*
    // 演示：在注解中写动态SQL(if 条件判断)
    @Update({
            "<script>", // 如果要写条件判断SQL，需要在外层加一组<script>标签
            "update login_ticket set status=#{status} ",
            "where ticket=#{ticket}",
            "<if test=\"ticket!=null\"> ",  // 注意内置双引号需要转义才能生效
            "and 1=1 ",
            "</if>",
            "</script>"
    })
     */
    int updateStatus(String ticket, int status);


}
