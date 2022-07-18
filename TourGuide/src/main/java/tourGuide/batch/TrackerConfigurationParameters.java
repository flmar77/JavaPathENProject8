package tourGuide.batch;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.StopWatch;

@Getter
@Setter
public class TrackerConfigurationParameters {
    private final long trackingPollingInterval;
    private final StopWatch stopWatch;

    public TrackerConfigurationParameters(long trackingPollingInterval, StopWatch stopWatch) {
        this.trackingPollingInterval = trackingPollingInterval;
        this.stopWatch = stopWatch;
    }

}
