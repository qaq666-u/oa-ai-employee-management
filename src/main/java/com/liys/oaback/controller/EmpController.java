package com.liys.oaback.controller;


import com.liys.oaback.common.Result;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Emp;
import com.liys.oaback.domain.EmpQuery;
import com.liys.oaback.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emp")
public class EmpController {


    @Autowired
    private EmpService empService;



    @GetMapping("/page")
    public Result page(EmpQuery query) {
        return Result.success(empService.page(query));
    }


    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        return Result.success(empService.get(id));
    }
    @PostMapping
    public Result add(@RequestBody Emp emp) {
        Emp created = empService.add(emp);
        return Result.success(Map.of(
                "empNo", created.getEmpNo(),
                "username", created.getUsername(),
                "initialPassword", "123456"
        ), "新增成功");
    }

    @PutMapping
    public Result update(@RequestBody Emp emp) {
        empService.update(emp);
        return Result.success(null, "修改成功");
    }


    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        empService.deleteBatch(ids);
        return Result.success(null, "批量删除成功");
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        empService.delete(id);
        return Result.success(null, "删除成功");
    }

    @PutMapping("/status/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        empService.updateStatus(id, status);
        return Result.success(null, "状态修改成功");
    }
}
