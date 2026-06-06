package com.chemistry.demo.config.scheduler;

import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.repository.UserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAccessScheduler {

    private final UserAccessRepository userAccessRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void expireUserAccess() {
        Instant now = Instant.now();

        List<UserAccess> expiredAccesses =
                userAccessRepository.findByStatusAndExpiredAtBefore(
                        AccessStatus.ACTIVE,
                        now
                );

        if (expiredAccesses.isEmpty()) {
            return;
        }

        for (UserAccess access : expiredAccesses) {
            access.setStatus(AccessStatus.EXPIRED);
        }

        userAccessRepository.saveAll(expiredAccesses);

        log.info("Expired {} user access record(s)", expiredAccesses.size());
    }
}