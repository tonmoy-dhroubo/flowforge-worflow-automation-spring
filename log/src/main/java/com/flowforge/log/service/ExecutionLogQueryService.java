package com.flowforge.log.service;

import com.flowforge.log.document.ExecutionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionLogQueryService {

    private final MongoTemplate mongoTemplate;

    public Page<ExecutionLog> search(
            UUID userId,
            UUID executionId,
            UUID eventId,
            UUID workflowId,
            String eventType,
            String status,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Criteria criteria = Criteria.where("userId").is(userId);

        if (executionId != null) {
            criteria = criteria.and("executionId").is(executionId);
        }
        if (eventId != null) {
            criteria = criteria.and("eventId").is(eventId);
        }
        if (workflowId != null) {
            criteria = criteria.and("workflowId").is(workflowId);
        }
        if (eventType != null && !eventType.isBlank()) {
            criteria = criteria.and("eventType").is(eventType);
        }
        if (status != null && !status.isBlank()) {
            criteria = criteria.and("status").is(status);
        }
        if (from != null || to != null) {
            Criteria timeCriteria = Criteria.where("timestamp");
            if (from != null) {
                timeCriteria = timeCriteria.gte(from);
            }
            if (to != null) {
                timeCriteria = timeCriteria.lte(to);
            }
            criteria = new Criteria().andOperator(criteria, timeCriteria);
        }

        Query query = new Query(criteria);
        if (pageable.getSort().isUnsorted()) {
            query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        }
        query.with(pageable);

        List<ExecutionLog> content = mongoTemplate.find(query, ExecutionLog.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ExecutionLog.class);
        return new PageImpl<>(content, pageable, total);
    }
}
