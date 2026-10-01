package com.liys.oaback.service.impl;

import com.liys.oaback.common.BusinessException;
import com.liys.oaback.utils.AiTools;
import com.liys.oaback.service.AiService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {
    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private static final String HISTORY_KEY = "aiHistory";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ObjectProvider<ChatClient> chatClientProvider;
    private final AiTools aiTools;

    @Value("${spring.ai.dashscope.api-key:}")
    private String apiKey;

    public AiServiceImpl(ObjectProvider<ChatClient> chatClientProvider, AiTools aiTools) {
        this.chatClientProvider = chatClientProvider;
        this.aiTools = aiTools;
    }

    @Override
    public String chat(String message, HttpSession session) {
        if (message == null || message.isBlank()) throw new BusinessException("请输入对话内容");
        List<Map<String, Object>> messages = history(session);
        messages.add(message("user", message));

        String reply = askModel(message);
        if (reply == null || reply.isBlank()) reply = buildReply(message);
        messages.add(message("assistant", reply));
        session.setAttribute(HISTORY_KEY, messages);
        return reply;
    }

    private String askModel(String message) {
        if (apiKey == null || apiKey.isBlank()) return null;
        try {
            ChatClient client = chatClientProvider.getIfAvailable();
            return client == null ? null : client.prompt().user(message).call().content();
        } catch (Exception e) {
            log.warn("DashScope AI call failed; using the built-in OA reply rules", e);
            return null;
        }
    }

    private String buildReply(String message) {
        String text = message.toLowerCase(Locale.ROOT);
        if (text.contains("部门") && (text.contains("多少") || text.contains("几个") || text.contains("总数"))) {
            return "公司当前共有 " + aiTools.countDepartments() + " 个部门。";
        }
        if (text.contains("部门") && (text.contains("哪些") || text.contains("列表") || text.contains("都有"))) {
            return aiTools.listDepartments();
        }
        if (text.contains("员工") && (text.contains("多少") || text.contains("几个人") || text.contains("总人数"))) {
            return "公司当前共有 " + aiTools.countEmployees() + " 名员工。";
        }
        String matchingDepartment = aiTools.findDepartmentNameIn(message);
        if (matchingDepartment != null) return aiTools.getDepartmentDetails(matchingDepartment);
        String matchingEmployee = aiTools.findEmployeeNameIn(message);
        if (matchingEmployee != null) return aiTools.findEmployee(matchingEmployee);
        if (text.contains("考勤")) {
            return "本月考勤统计：应出勤 22 天，全员平均出勤 21.6 天，迟到 3 人次，请假 5 人次，整体出勤率 98.2%。考勤统计为示例信息，需以实际考勤数据为准。";
        }
        if (text.contains("周报")) {
            return "本周工作周报摘要：\n1. 完成员工管理模块前后端联调；\n2. 修复部门删除时的关联校验问题；\n3. 优化系统交互体验。\n\n下周计划：完善 AI 对话与权限控制。";
        }
        if (text.contains("请假")) {
            return "公司请假制度示例：年假按工龄核定，事假建议提前申请，病假按公司流程提交证明材料。请假申请请通过 OA 系统提交并等待审批；具体天数和要求请以公司现行制度为准。";
        }
        if (text.contains("效率")) {
            return "提升工作效率的建议：\n1. 每天列出最重要的三项任务；\n2. 为专注工作安排不被打断的时间；\n3. 控制会议时长并记录行动项；\n4. 将重复工作整理成可复用流程。";
        }
        return "已收到你的问题：“" + message + "”。我是 OA 智能助手，可以回答办公效率、请假制度、周报等问题，也可以查询部门和员工基础信息。";
    }

    private Map<String, Object> message(String role, String content) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("role", role);
        item.put("content", content);
        item.put("time", LocalTime.now().format(TIME_FORMATTER));
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> history(HttpSession session) {
        Object value = session.getAttribute(HISTORY_KEY);
        return value instanceof List<?> list ? (List<Map<String, Object>>) list : new ArrayList<>();
    }

    @Override
    public void clear(HttpSession session) {
        session.removeAttribute(HISTORY_KEY);
    }
}
