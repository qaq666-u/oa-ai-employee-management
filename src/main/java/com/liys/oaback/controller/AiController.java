package com.liys.oaback.controller;

import com.liys.oaback.common.Result;
import com.liys.oaback.service.AiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, String> body, HttpSession session) {
        String reply = aiService.chat(body == null ? null : body.get("message"), session);
        return Result.success(Map.of("reply", reply));
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> history(HttpSession session) {
        return Result.success(aiService.history(session));
    }

    @DeleteMapping("/history")
    public Result<Void> clear(HttpSession session) {
        aiService.clear(session);
        return Result.success(null, "聊天记录已清空");
    }
}
