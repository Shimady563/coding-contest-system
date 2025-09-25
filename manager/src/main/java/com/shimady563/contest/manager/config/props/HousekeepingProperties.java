package com.shimady563.contest.manager.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "housekeeping")
public class HousekeepingProperties {
    private int solutionKeepDays = 30;
}
