package com.flowforge.executor.plugin.impl;
import com.flowforge.executor.plugin.ActionPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Component @Slf4j
public class SlackAction implements ActionPlugin {
    @Override public String getSupportedType() { return "SLACK_MESSAGE"; }
    @Override public Mono<Map<String, Object>> execute(Map<String, Object> config, Map<String, Object> payload) {
        return Mono.fromCallable(() -> {
            log.info("[MOCK SLACK] Sending to {}: {}", config.get("channel"), config.get("message"));
            Map<String, Object> result = new HashMap<>();
            result.put("sent", true);
            return result;
        });
    }
}