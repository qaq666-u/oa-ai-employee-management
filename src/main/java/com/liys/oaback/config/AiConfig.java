package com.liys.oaback.config;

import com.liys.oaback.utils.AiTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    private static final String SYSTEM_PROMPT = """
            你是 OA 智能助手，由阿里巴巴通义千问 qwen-plus 模型驱动。
            请用简洁清晰的中文回答。你可以使用工具查询公司的真实部门和员工基础信息。
            涉及公司部门或员工数据时，必须先调用工具，严禁编造姓名、人数或部门信息。
            只返回工具提供的非敏感基础信息，不得泄露密码、手机号、邮箱等个人信息。
            不确定时请向用户澄清。不得讨论政治、宗教或提供法律意见。
            """;

    @Bean
    @ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${spring.ai.dashscope.api-key:}')"
            + " && '${spring.ai.model.chat:none}' == 'dashscope'"
            + " && '${spring.ai.dashscope.enabled:false}' == 'true'")
    public ChatClient oaChatClient(ChatClient.Builder builder, AiTools aiTools) {
        return builder.defaultSystem(SYSTEM_PROMPT).defaultTools(aiTools).build();
    }
}
