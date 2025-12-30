package com.flowforge.executor.plugin.impl;
import com.flowforge.executor.plugin.ActionPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Component @Slf4j
public class GoogleSheetsAction implements ActionPlugin {
    @Override public String getSupportedType() { return "GOOGLE_SHEET_ROW"; }
    @Override public Mono<Map<String, Object>> execute(Map<String, Object> config, Map<String, Object> payload) {
        return Mono.fromCallable(() -> {
            log.info("[MOCK SHEETS] Appending to Sheet ID: {}", config.get("spreadsheetId"));
            Map<String, Object> result = new HashMap<>();
            result.put("status", "UPDATED");
            return result;
        });
    }
}