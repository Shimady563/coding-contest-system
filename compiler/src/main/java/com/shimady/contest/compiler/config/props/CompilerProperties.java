package com.shimady.contest.compiler.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "compiler")
public class CompilerProperties {
    private Long timeoutSeconds = 10L;
    private Integer maxOutputBytes = 1024 * 1024;
    private String workdir = "/tmp";
}
