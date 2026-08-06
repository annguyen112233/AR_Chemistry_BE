package com.chemistry.demo.repository;

import com.chemistry.demo.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SystemLogRepository
        extends JpaRepository<SystemLog, Long>, JpaSpecificationExecutor<SystemLog> {

    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.loggedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}
