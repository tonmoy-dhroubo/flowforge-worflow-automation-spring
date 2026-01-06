package com.flowforge.log.document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "execution_logs")
public class ExecutionLog {
    @Id private String id;
    private UUID userId;
    private UUID executionId;
    private UUID eventId;
    private UUID workflowId;
    private String eventType;
    private String status;
    private Map<String, Object> data;
    private Instant timestamp;
}
