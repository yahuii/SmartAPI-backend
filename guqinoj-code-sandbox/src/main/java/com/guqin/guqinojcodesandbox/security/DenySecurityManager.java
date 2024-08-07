package com.guqin.guqinojcodesandbox.security;

import java.security.Permission;

/**
 * @author 顾琴
 */
public class DenySecurityManager extends SecurityManager{

    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException(perm.getActions());
    }
}
