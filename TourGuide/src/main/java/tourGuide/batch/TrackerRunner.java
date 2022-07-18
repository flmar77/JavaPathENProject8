package tourGuide.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tourGuide.domain.service.TourGuideService;

@Component
public class TrackerRunner implements CommandLineRunner {

    private final TourGuideService tourGuideService;

    public TrackerRunner(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @Override
    public void run(String... args) {
        Tracker tracker = new Tracker(tourGuideService);
        //addShutDownHook();
    }
}
