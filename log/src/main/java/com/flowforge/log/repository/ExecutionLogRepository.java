package com.flowforge.log.repository;
import com.flowforge.log.document.ExecutionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository public interface ExecutionLogRepository extends MongoRepository<ExecutionLog, String> {}