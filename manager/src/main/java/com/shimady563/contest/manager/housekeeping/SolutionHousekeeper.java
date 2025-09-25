package com.shimady563.contest.manager.housekeeping;

import com.shimady563.contest.manager.config.props.HousekeepingProperties;
import com.shimady563.contest.manager.repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolutionHousekeeper implements Housekeeper {
    private final HousekeepingProperties properties;
    private final SolutionRepository solutionRepository;

    @Override
    public void sweep() {
        log.info("Deleting solutions older than {} days", properties.getSolutionKeepDays());
        solutionRepository.deleteBySubmittedAtBefore(LocalDateTime.now().minusDays(properties.getSolutionKeepDays()));
    }
}
