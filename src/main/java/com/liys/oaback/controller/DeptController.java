package com.liys.oaback.controller;

import com.liys.oaback.common.Result;
import com.liys.oaback.domain.CommonQuery;
import com.liys.oaback.domain.Dept;
import com.liys.oaback.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;
  //  浏览器的变化而变化     明显的裸漏在外面的请求链接
    @GetMapping("/page")
    // 分页查询
    public Result page(CommonQuery commonQuery){

         return Result.success(deptService.selectPage(commonQuery));
    }

    @GetMapping("/list")
    // 查询全部
    public Result list(){

        return Result.success(deptService.selectList());
    }

     @PostMapping
    public Result add(@RequestBody Dept dept){

          deptService.add(dept);
          return  Result.success(null,"添加成功");
     }
     @PutMapping
     public Result update(@RequestBody Dept dept){
          deptService.update(dept);
         return  Result.success(null,"修改成功");
     }
     // restful风格 删除   id做浏览器的参数  http:localhost:8083/dept/1
     @DeleteMapping("/{id}")
     public Result delete(@PathVariable Long id){
          deptService.delete(id);
        return Result.success("null","删除成功");
     }


}
