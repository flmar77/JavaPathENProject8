package tourGuide.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tourGuide.domain.model.User;
import tourGuide.domain.service.TourGuideService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class Tracker extends Thread {
    private final TourGuideService tourGuideService;
    private final ExecutorService executorService;
    private final TrackerConfigurationParameters trackerConfigurationParameters;

    public Tracker(TourGuideService tourGuideService, TrackerConfigurationParameters trackerConfigurationParameters) {
        this.tourGuideService = tourGuideService;
        this.trackerConfigurationParameters = trackerConfigurationParameters;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void startTracker() {
        log.debug("start tracker");
        executorService.submit(this);
    }

    @Override
    public void run() {

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                log.debug("Tracker stopping");
                break;
            }

            List<User> users = tourGuideService.getAllUsers();

            log.debug("Begin Tracker. Tracking " + users.size() + " users.");
            trackerConfigurationParameters.getStopWatch().start();

            users.parallelStream().forEach(tourGuideService::trackUserLocation);

            trackerConfigurationParameters.getStopWatch().stop();
            log.debug("Tracker Time Elapsed: " + trackerConfigurationParameters.getStopWatch().getTime() + " ms.");

            trackerConfigurationParameters.getStopWatch().reset();

            try {
                log.debug("Tracker sleeping");
                TimeUnit.SECONDS.sleep(trackerConfigurationParameters.getTrackingPollingInterval());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // TODO : move to TestHelper class ?
    public void stopTracking() {
        executorService.shutdownNow();
    }
}
