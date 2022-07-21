package tourGuide;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tourGuide.batch.TrackerDaemon;
import tourGuide.dal.TourGuideFakeRepo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class InitApplication implements CommandLineRunner {

    private final TrackerDaemon trackerDaemon;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final TourGuideFakeRepo tourGuideFakeRepo;
    private final boolean testMode = true;

    public InitApplication(TrackerDaemon trackerDaemon, TourGuideFakeRepo tourGuideFakeRepo) {
        this.trackerDaemon = trackerDaemon;
        this.tourGuideFakeRepo = tourGuideFakeRepo;
    }

    @Override
    public void run(String... args) {
        if (testMode) {
            log.info("TestMode enabled");
            tourGuideFakeRepo.initializeInternalUsers(10);
        }

        executorService.submit(trackerDaemon);
    }
}
