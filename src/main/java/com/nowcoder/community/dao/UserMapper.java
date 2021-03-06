package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 数据访问接口，不需要写实现类，底层会自动实现接口(前提是将CRUD所依赖的SQL告诉它，使用配置文件)

@Mapper // 用来标识Bean。也可以用 @Repository, @Mapper 是 MyBatis的注解
//@Repository
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    //返回插入User的主键
    int insertUser(User user);

    // 返回影响的行数
    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
