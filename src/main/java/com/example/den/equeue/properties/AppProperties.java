package com.example.den.equeue.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Duration cycleDuration;
    private Duration bookingRoundDuration;

}
