package com.qbk.entity;

import lombok.Data;

import java.util.List;

/**
 * user实体
 */
@Data
public class SysUser {
    private String id;
    private String username;
    private String password;
    private List<SysRole> roles;
}
