package com.flowforge.executor.controller;

import com.flowforge.executor.plugin.ActionPlugin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/executor")
@RequiredArgsConstructor
public class ExecutorController {

    private final List<ActionPlugin> plugins;

    @GetMapping("/plugins")
    public ResponseEntity<Map<String, Object>> listPlugins() {
        List<String> supportedTypes = plugins.stream()
                .map(ActionPlugin::getSupportedType)
                .sorted()
                .toList();
        return ResponseEntity.ok(Map.of("supportedActionTypes", supportedTypes));
    }
}

