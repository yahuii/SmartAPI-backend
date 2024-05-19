package com.huiapi.model.dto.user;

import lombok.Data;

/**
 * 用户更新请求类
 * @author 顾琴
 */

@Data
public class UserUpdateRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别（0-男，1-女）
     */
    private Integer gender;

    /**
     * 用户状态（0-启用，1-禁用）
     */
    private Integer status;

    /**
     * 用户角色：user / admin
     */
    private String userRole;
}
