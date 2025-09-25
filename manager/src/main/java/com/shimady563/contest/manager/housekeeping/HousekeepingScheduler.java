package com.shimady563.contest.manager.housekeeping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HousekeepingScheduler {
    private final List<Housekeeper> housekeepers;

    // at 3 AM every Sunday
    @Scheduled(cron = "0 0 3 * * 0", zone = "Europe/Moscow")
    public void scheduleHousekeeping() {
        log.info("Starting housekeeping...");
        for (Housekeeper housekeeper : housekeepers) {
            try {
                housekeeper.sweep();
            } catch (Exception e) {
                log.warn("Error during housekeeping", e);
            }
        }
        log.info("Housekeeping completed.");
    }
}
