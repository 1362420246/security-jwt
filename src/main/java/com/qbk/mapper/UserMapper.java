package com.qbk.mapper;

import com.qbk.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: quboka
 */
@Mapper
public interface UserMapper {

    SysUser findByUsername(@Param("username") String username) ;

    int insert(SysUser userToAdd);
}
