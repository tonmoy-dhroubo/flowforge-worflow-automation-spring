package com.flowforge.executor.plugin;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PluginManager {
    private final Map<String, ActionPlugin> plugins = new HashMap<>();
    public PluginManager(List<ActionPlugin> actionPlugins) {
        for (ActionPlugin plugin : actionPlugins) {
            plugins.put(plugin.getSupportedType(), plugin);
        }
    }
    public Optional<ActionPlugin> getPlugin(String type) {
        return Optional.ofNullable(plugins.get(type));
    }
}