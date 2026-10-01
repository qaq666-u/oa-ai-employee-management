package com.liys.oaback.controller;

import com.liys.oaback.common.Result;
import com.liys.oaback.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    // 引入service层
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> body, HttpSession session) {
        return Result.success(userService.login(body == null ? null : body.get("username"),
                body == null ? null : body.get("password"), session), "登录成功");
    }
    // 退出系统
    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        // 从会话中剔除登录用户
        session.invalidate();
        // 发消息  给前端
        return Result.success(null, "退出成功");
    }


    // 获取当前登录用户的信息
    @GetMapping("/info")
    public Result info(HttpSession session) {
        return Result.success(userService.info(session));
    }
    @PutMapping("/password")
    public Result changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        userService.changePassword(body.get("oldPassword"), body.get("newPassword"), session);
        return Result.success(null, "密码修改成功");
    }

    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        return Result.success(userService.updateAvatar(file, session), "头像上传成功");
    }

}
