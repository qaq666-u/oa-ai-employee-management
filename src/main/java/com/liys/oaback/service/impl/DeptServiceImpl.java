package com.liys.oaback.service.impl;

import com.liys.oaback.common.PageResult;
import com.liys.oaback.common.BusinessException;
import com.liys.oaback.dao.DeptMapper;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Dept;
import com.liys.oaback.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    // dao曾
    @Autowired
    private DeptMapper deptMapper;


    @Override
    public PageResult<Dept> selectPage(CommonQuery commonQuery) {
     // 分页    全部数据查询
    // .var idea自动的显示帮你写 返回的对象
        List<Dept> depts = deptMapper.selectPage(commonQuery);
        // 查询总条数
        long total = deptMapper.countByQuery(commonQuery);

        // 分页的对象  depts 全部部门数   总条数
        return new PageResult<>(depts, total, commonQuery.getPage(), commonQuery.getPageSize());
    }




    @Override
    public List<Dept> selectList() {


        return deptMapper.selectAll();
    }

/**
 * Updates a department's information in the system.
 * This method is an override of the base class method to provide specific functionality for updating department details.
 *
 * @param dept The Department object containing the updated information to be saved
 */
    @Override
    public void update(Dept dept) {
        if (dept == null || dept.getId() == null) throw new BusinessException("部门ID不能为空");
        validate(dept);
        if (dept.getName() == null && dept.getDescription() == null) throw new BusinessException("请至少提供一个要修改的字段");
        if (deptMapper.update(dept) == 0) throw new BusinessException("部门不存在或未修改");

    // Method implementation would go here
    // Currently, this is a placeholder method with no implementation
    }

    public void add(Dept d) {
        if (d == null) throw new BusinessException("部门信息不能为空");
        validate(d);
        deptMapper.insert(d);
    }

    private void validate(Dept dept) {
        if (dept.getName() != null) {
            if (dept.getName().isBlank()) throw new BusinessException("部门名称不能为空");
            if (dept.getName().length() > 50) throw new BusinessException("部门名称不能超过50个字符");
        }
        if (dept.getDescription() != null && dept.getDescription().length() > 255) {
            throw new BusinessException("部门描述不能超过255个字符");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new BusinessException("部门ID不能为空");
        if (deptMapper.countEmpByDeptId(id) > 0) throw new BusinessException("该部门下存在员工，禁止删除");
        if (deptMapper.deleteById(id) == 0) throw new BusinessException("部门不存在");
    }
}
