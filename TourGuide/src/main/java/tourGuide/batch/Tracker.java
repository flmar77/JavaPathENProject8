package tourGuide.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import tourGuide.domain.model.User;
import tourGuide.domain.service.TourGuideService;

import java.util.List;

@Slf4j
@Service
public class Tracker {

    private final TourGuideService tourGuideService;
    private final StopWatch stopWatch = new StopWatch();

    public Tracker(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    public long trackOnce() {
        long watchTime;
        List<User> users = tourGuideService.getAllUsers();
        log.debug("Begin Tracker. Tracking " + users.size() + " users.");

        stopWatch.start();
        users.parallelStream().forEach(tourGuideService::trackUserLocation);
        stopWatch.stop();

        watchTime = stopWatch.getTime();
        stopWatch.reset();

        return watchTime;
    }
}
