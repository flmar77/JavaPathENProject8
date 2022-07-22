package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.batch.Tracker;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.User;
import tourGuide.domain.service.RewardsService;
import tourGuide.domain.service.TourGuideService;
import tripPricer.TripPricer;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@Slf4j
public class TourGuideServicePerformanceITest {

    private final GpsUtil gpsUtil = new GpsUtil();
    private final TripPricer tripPricer = new TripPricer();
    private final RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private static final TourGuideFakeRepo tourGuideFakeRepo = new TourGuideFakeRepo();
    private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer, tourGuideFakeRepo);

    @BeforeClass
    public static void setUpAllTests() {
        Locale.setDefault(Locale.US);
        // Users could be incremented up to 100,000 for perf test purpose
        tourGuideFakeRepo.initializeInternalUsers(1000);
    }

    @Test
    public void highVolumeTrackLocation() {
        Tracker tracker = new Tracker(tourGuideService);

        long watchTime = tracker.trackAllUsers();

        log.debug("highVolumeTrackLocation: Time Elapsed: " + watchTime + " ms");
        assertTrue(TimeUnit.MINUTES.toMillis(15) >= watchTime);
    }

    @Test
    public void highVolumeCalculateRewards() {
        StopWatch stopWatch = new StopWatch();
        Attraction fakeAttraction = new Attraction("attractionName", "city", "state", 0, 0);
        Date fakeDate = new Date();
        List<User> allUsers = tourGuideService.getAllUsers();
        allUsers.parallelStream()
                .forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), fakeAttraction, fakeDate)));

        stopWatch.start();
        allUsers.forEach(rewardsService::calculateRewards);
        stopWatch.stop();
        long watchTime = stopWatch.getTime();

        log.debug("highVolumeCalculateRewards: Time Elapsed: " + watchTime + " ms");
        assertTrue(TimeUnit.MINUTES.toMillis(20) >= watchTime);
    }

}
