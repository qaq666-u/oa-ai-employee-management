package com.liys.oaback.service;

import com.liys.oaback.common.PageResult;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Dept;

import java.util.List;

public interface DeptService {

    // 分页
   PageResult<Dept> selectPage(CommonQuery commonQuery);

   void add(Dept d);

   // 查询全部
   List<Dept> selectList();

   void update(Dept dept);

   void delete(Long id);


}
