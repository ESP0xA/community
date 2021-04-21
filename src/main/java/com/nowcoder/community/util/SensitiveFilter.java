package com.nowcoder.community.util;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SensitiveFilter {

    // 初始化前缀树

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 敏感字符替换字符
    private static final String REPLACEMENT = "***";
    // 根节点
    private TrieNode trieNode = new TrieNode();
    // 需要在程序启动的时候自动初始化完毕
    // @PostConstruct 在容器实例化该bean后，即在调用对应类构造方法之后，该方法被自动调用
    @PostConstruct
    public void init() {
        // 读文件字符
        // this.getClass()  任意对象都可以获取它的类
        // getClassLoader() 获取类加载器（从target目录下的class目录中获取。代码被编译之后，所有代码都会被编译到class目录下，包括敏感词的配置文件）
        this.getClass().getClassLoader().getResourceAsStream("");
    }


    // 定义前缀树
    private class TrieNode {

        // 描述关键词是否结束的标志
        private boolean isKeywordEnd = false;

        // 描述当前节点的子节点（Key: 子节点字符 - value: 子节点）
        private Map<Character, TrieNode> subNode = new HashMap<>();



        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode (Character c, TrieNode node) {
            subNode.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c, TrieNode node) {
            return subNode.get(c);
        }
    }
}
