package com.nowcoder.community.util;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
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
    private TrieNode rootNode = new TrieNode();
    // 需要在程序启动的时候自动初始化完毕
    // @PostConstruct 在容器实例化该bean后，即在调用对应类构造方法之后，该方法被自动调用
    @PostConstruct
    public void init() {
        // 读文件字符
        // this.getClass()  任意对象都可以获取它的类
        // getClassLoader() 获取类加载器（从target目录下的class目录中获取。代码被编译之后，所有代码都会被编译到class目录下，包括敏感词的配置文件）
        // getResourceAsStream() 得到字节流
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");   // 创建对象过程写在try后的括号中，编译时自动加 finally关闭
                // 输入流转换为字符流，再装换为缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            // 通过reader读取每一个敏感词
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    // 将敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);

            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {      //  说明当前节点无子节点，否则此次添加的字符串和之前的某次字符串存在公共前缀.
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指针指向子节点
            tempNode = subNode;
            // 设置结束标志
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text  待过滤文本
     * @return  过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // ptr 1
        TrieNode tempNode = rootNode;
        // ptr 2
        int recorder = 0;
        // ptr 3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过特殊字符
            if (isSymbol(c)) {
                // 当ptr1处于根节点，任意字符都是合法的
                if (tempNode == rootNode) {     // 只有在匹配到疑似敏感词（即和敏感词存在公共前缀）时，忽略其后的特殊字符
                    sb.append(c);
                    recorder++;
                }
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) { // else 找到匹配字符
                // 以recorder开头的不是敏感词，只记录recorder位，因为可能以recorder下一位开头的存在敏感词
                sb.append(text.charAt(recorder));
                position = ++recorder;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 找到一个以recorder开头的敏感词
                sb.append(REPLACEMENT);
                recorder = ++position;
                tempNode = rootNode;
            } else {
                ++position;
            }

            // 处理position指针溢出情况
            // 当position指针溢出时，如果直接添加以recorder开头的substring，那么当：
            // position指针匹配了某个敏感词的前缀，恰好溢出，同时敏感词列表中存在一个为当前敏感词的子串的敏感词时，就会该子串敏感词就会被忽略
            // 这是教程算法中的一个不完善的地方，在此做出修正。
            if (position >= text.length()) {
                if (recorder < text.length()) {
                    sb.append(text.charAt(recorder));
                }
                position = ++recorder;
                tempNode = rootNode;
            }
        }
        // 将最后一批字符计入结果
        //sb.append(text.substring(recorder));


        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        // 0x2E80 到 0x9FFF为东亚字符范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
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
        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }
    }
}
