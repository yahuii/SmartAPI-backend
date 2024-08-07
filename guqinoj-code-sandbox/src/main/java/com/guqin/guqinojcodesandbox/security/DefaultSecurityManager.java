package com.guqin.guqinojcodesandbox.security;

import java.security.Permission;

/**
 * 默认安全管理器
 * @author 顾琴
 */
public class DefaultSecurityManager extends SecurityManager{

    //检查所有的权限

    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认不做任何限制");
        
        super.checkPermission(perm);
    }
}
