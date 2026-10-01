package com.liys.oaback.service;

import com.liys.oaback.common.PageResult;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.EmpQuery;

import java.util.List;

public interface EmpService {

    // 分页查询
    PageResult<Emp> page(EmpQuery empQuery);

    // 根据id查询
    Emp get(Long id);

    // 新增用户
    Emp add(Emp e);

    // 修改用户
    void update(Emp e);

    // 删除用户
    void delete(Long id);
    //

    void deleteBatch(List<Long> ids);

    // 更新用户的状态
    void updateStatus(Long id, Integer status);

}
