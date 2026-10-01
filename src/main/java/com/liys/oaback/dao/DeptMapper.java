package com.liys.oaback.dao;

import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptMapper {

    List<Dept> selectPage(CommonQuery query);

    long countByQuery(CommonQuery query);

  // 查全部的
    List<Dept> selectAll();
 // 查询单个的
    Dept selectById(Long id);

    int insert(Dept dept);
    int deleteById(Long id);

    // 目标部门的员工人数
    Long countEmpByDeptId(Long id);


/**
 * Updates an existing department record in the database
 *
 * @param dept The Department object containing updated information
 * @return An integer representing the number of rows affected by the update operation
 */
    int update(Dept dept);


}
