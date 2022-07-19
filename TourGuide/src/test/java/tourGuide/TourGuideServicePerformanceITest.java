package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.batch.Tracker;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.User;
import tourGuide.domain.service.RewardsService;
import tourGuide.domain.service.TourGuideService;
import tripPricer.TripPricer;

import java.util.ArrayList;
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
    private final TourGuideFakeRepo tourGuideFakeRepo = new TourGuideFakeRepo();
    private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer, tourGuideFakeRepo);

    @Before
    public void setUpAllTests() {
        Locale.setDefault(Locale.US);
        // TODO : Users should be incremented up to 100,000
        tourGuideFakeRepo.initializeInternalUsers(100);
    }

    @Test
    public void highVolumeTrackLocation() {
        Tracker tracker = new Tracker(tourGuideService);

        long watchTime = tracker.trackOnce();

        log.debug("highVolumeTrackLocation: Time Elapsed: " + watchTime + " ms");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(watchTime));
    }

    @Ignore
    @Test
    public void highVolumeGetRewards() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = new ArrayList<>();
        allUsers = tourGuideService.getAllUsers();
        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

        allUsers.forEach(rewardsService::calculateRewards);

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
