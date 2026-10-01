package com.liys.oaback.utils;

import com.liys.oaback.dao.DeptMapper;
import com.liys.oaback.dao.EmpMapper;
import com.liys.oaback.domain.Dept;
import com.liys.oaback.domain.Emp;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiTools {
    private final DeptMapper deptMapper;
    private final EmpMapper empMapper;

    public AiTools(DeptMapper deptMapper, EmpMapper empMapper) {
        this.deptMapper = deptMapper;
        this.empMapper = empMapper;
    }

    @Tool(description = "查询公司当前部门总数。当用户问有多少个部门、部门数量时调用。")
    public int countDepartments() {
        return deptMapper.selectAll().size();
    }

    public String findDepartmentNameIn(String message) {
        if (message == null) return null;
        return deptMapper.selectAll().stream().map(Dept::getName)
                .filter(name -> name != null && message.contains(name)).findFirst().orElse(null);
    }

    public String findEmployeeNameIn(String message) {
        if (message == null) return null;
        return empMapper.selectAll().stream()
                .filter(e -> (e.getName() != null && message.contains(e.getName()))
                        || (e.getUsername() != null && message.contains(e.getUsername())))
                .map(e -> e.getName() != null && message.contains(e.getName()) ? e.getName() : e.getUsername())
                .findFirst().orElse(null);
    }

    @Tool(description = "列出公司所有部门的 ID 和名称。当用户问有哪些部门、部门列表时调用。")
    public String listDepartments() {
        List<Dept> depts = deptMapper.selectAll();
        if (depts.isEmpty()) return "公司暂无部门数据。";
        return depts.stream().map(d -> d.getId() + " - " + d.getName()).collect(Collectors.joining("\n"));
    }

    @Tool(description = "根据部门名称查询该部门员工数量和基本信息。")
    public String getDepartmentDetails(@ToolParam(description = "部门名称，例如技术部") String deptName) {
        if (deptName == null || deptName.isBlank()) return "请提供部门名称。";
        Dept dept = deptMapper.selectAll().stream()
                .filter(d -> d.getName() != null && d.getName().contains(deptName.trim()))
                .findFirst().orElse(null);
        if (dept == null) return "没有找到名为“" + deptName + "”的部门。";
        long count = empMapper.selectAll().stream().filter(e -> dept.getId().equals(e.getDeptId())).count();
        return String.format("部门：%s，ID：%d，员工数：%d 人", dept.getName(), dept.getId(), count);
    }

    @Tool(description = "查询公司当前员工总数。当用户问公司有多少员工、总人数时调用。")
    public int countEmployees() {
        return empMapper.selectAll().size();
    }

    @Tool(description = "根据姓名或登录账号查询员工基础信息。不得返回密码、手机号或邮箱。")
    public String findEmployee(@ToolParam(description = "员工姓名或登录账号") String keyword) {
        if (keyword == null || keyword.isBlank()) return "请提供员工姓名或登录账号。";
        List<Emp> matched = empMapper.selectAll().stream()
                .filter(e -> (e.getName() != null && e.getName().contains(keyword.trim()))
                        || (e.getUsername() != null && e.getUsername().contains(keyword.trim())))
                .limit(5).toList();
        if (matched.isEmpty()) return "没有找到匹配的员工。";
        return matched.stream()
                .map(e -> String.format("%s（工号 %s，部门 ID=%s，职位 ID=%s）",
                        e.getName(), e.getEmpNo(), e.getDeptId(), e.getJobId()))
                .collect(Collectors.joining("\n"));
    }
}
