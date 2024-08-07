package com.guqin.guqinojcodesandbox.security;

import java.io.FileDescriptor;
import java.security.Permission;

/**
 * @author 顾琴
 */
public class MySecurityManager extends SecurityManager{
    //检测是否有权限
//    @Override
//    public void checkPermission(Permission perm) {
//        throw new SecurityException("checkPermission权限异常" + perm.getActions());
//    }

    //检测是否可执行
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec权限异常" + cmd);
    }

    //检测是否有权限读文件
    @Override
    public void checkRead(String file) {
        throw new SecurityException("checkRead权限异常" + file);
    }

    //检测是否有权限写文件
    @Override
    public void checkWrite(String file) {
        throw new SecurityException("checkWrite权限异常" + file);
    }

    //检测是否有权限链接网络
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("checkConnect权限异常" + host);
    }
}
