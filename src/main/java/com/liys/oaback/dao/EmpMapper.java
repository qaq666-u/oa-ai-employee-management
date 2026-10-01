package com.liys.oaback.dao;

import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.EmpQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@Mapper
public interface EmpMapper {

/**
 * Selects an employee by their username
 *
 * @param username The username of the employee to be retrieved
 * @return Emp object representing the employee with the given username,
 *         or null if no employee with that username exists
 */
List<Emp> selectPage(EmpQuery query);

    List<Emp> selectAll();

    long countByQuery(EmpQuery query);

    Emp selectById(Long id);

    Emp selectByUsername(String username);

    // 新增员工
    int insert(Emp emp);
// 更新员工
    int update(Emp emp);
// 根据id删除员工
    int deleteById(Long id);
    // 批量删除员工
    int deleteBatch(@Param("ids") List<Long> ids);
 // 更新员工状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateIdentity(@Param("id") Long id, @Param("empNo") String empNo, @Param("username") String username);
    // 更新员工头像
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

}
