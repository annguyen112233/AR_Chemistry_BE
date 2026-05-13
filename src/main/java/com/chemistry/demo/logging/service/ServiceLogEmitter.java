package com.chemistry.demo.logging.service;

import com.chemistry.demo.logging.PerformanceLog;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceLogEmitter {

    public void emit(PerformanceLog event, Throwable throwable) {
        var marker = Markers.appendEntries(event.toMap());
        if (throwable != null) {
            log.error(marker, event.getMessage(), throwable);
            return;
        }

        if ("WARN".equals(event.getLevel())) {
            log.warn(marker, event.getMessage());
            return;
        }

        log.info(marker, event.getMessage());
    }
}
