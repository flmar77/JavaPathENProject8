package tourGuide.batch;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrackerDefaultConfiguration {

    @Bean
    public TrackerConfigurationParameters getTrackerConfiguration() {
        // TODO : 5 minutes
        return new TrackerConfigurationParameters(10, new StopWatch());
    }
}
