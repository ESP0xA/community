package com.nowcoder.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityIndexApplication.class)
public class RedisTests {

    // 注入template
    @Autowired
    private RedisTemplate redisTemplate;

    // 测试String类型数据访问
    @Test
    public void testStrings() {
        String redisKey = "test:count";

        // 操作值
        redisTemplate.opsForValue().set(redisKey, 1);   // set value to 1
        System.out.println(redisTemplate.opsForValue().get(redisKey));  // get value by key
        System.out.println(redisTemplate.opsForValue().increment(redisKey));    // 增加count by key
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));    // 减少count by key
    }

    // 测试Hash类型的数据访问
    @Test
    public void testHashes() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    // 测试列表类型数据访问
    @Test
    public void testLists() {
        String redisKey = "test:ids";
        // 从左边入
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);
        // 访问数据
        System.out.println(redisTemplate.opsForList().size(redisKey));  // gets size
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));  // gets data index of 0
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));  // gets data from index of 0 to 2
        // 出
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));  // left pop
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));  // left pop
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));  // left pop

    }

    // 测试集合类型的数据访问
    @Test
    public void testSets() {
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));   // gets size of set
        System.out.println(redisTemplate.opsForSet().pop(redisKey));   // pop random element from set
        System.out.println(redisTemplate.opsForSet().members(redisKey));   // gets all elements of set
    }

    // 测试有序集合类型数据访问
    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        // 有序集合默认由小到大排序
        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey)); // 统计数据
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));   // 统计某个实体的score
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "八戒"));   // 统计某个实体的rank
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));   // 统计某个实体的rank（倒序排列的rank）
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));       // 取rank在某范围内的实体
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));       // 取rank在某范围内的实体（rank 按倒序排列）
    }

    // 测试key的访问
    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");  // 删除key
        System.out.println(redisTemplate.hasKey("test:user"));  // 判断有无key

        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);  // 设置key的过期时间
    }

    // 在一开始创建redis访问对象的时候就将key绑定进去
    // 用该对象代替重复使用key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();

        System.out.println(operations.get());
    }

    // Redis事务
    // 编程式事务

    @Test
    public void testTransactional() {

        // 处理事务也是调用redisTemplate
        Object object = redisTemplate.execute(new SessionCallback() {   // 方法需要传递一个接口的实例
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException { // 当redisTemplate的execute执行时spring自动调用
                String redisKey = "test:tx";

                redisOperations.multi();    // 启用事务

                // 在启用事务和提交事务之前，命令会被置于队列中不会被立即执行
                redisOperations.opsForSet().add(redisKey, "zhangs");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");

                System.out.println(redisOperations.opsForSet().members(redisKey));  // 无法查到结果
                return redisOperations.exec();  // 提交事务
            }
        });
        System.out.println(object);
    }

}
