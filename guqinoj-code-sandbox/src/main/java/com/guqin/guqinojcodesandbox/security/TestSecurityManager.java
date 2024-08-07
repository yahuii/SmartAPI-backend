package com.guqin.guqinojcodesandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class TestSecurityManager {

    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());
        FileUtil.readLines("D:\\code\\huiapi-backend\\guqinoj-code-sandbox\\src\\main\\resources\\testCode\\simpleComputeArgs\\Main.java", StandardCharsets.UTF_8);
    }
}
