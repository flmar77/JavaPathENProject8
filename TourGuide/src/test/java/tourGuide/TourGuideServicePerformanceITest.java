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
import tourGuide.batch.TrackerConfigurationParameters;
import tourGuide.dal.InternalTestHelper;
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

    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    private final TrackerConfigurationParameters trackerConfigurationParameters = new TrackerConfigurationParameters(900000, new StopWatch());
    private final GpsUtil gpsUtil = new GpsUtil();
    private final TripPricer tripPricer = new TripPricer();
    private final RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer);

    @Before
    public void setUpAllTests() {
        Locale.setDefault(Locale.US);
        // TODO : Users should be incremented up to 100,000
        InternalTestHelper.setInternalUserNumber(100000);
        tourGuideService.initializeInternalUsers();
        log.debug(String.valueOf(InternalTestHelper.getInternalUserNumber()));
    }

    @Test
    public void highVolumeTrackLocation() {
        Tracker tracker = new Tracker(tourGuideService, trackerConfigurationParameters);

        tracker.trackOnce();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + trackerConfigurationParameters.getStopWatch().getTime() + " ms");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(trackerConfigurationParameters.getStopWatch().getTime()));
    }

    @Ignore
    @Test
    public void highVolumeGetRewards() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Tracker tracker = new Tracker(tourGuideService, trackerConfigurationParameters);

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = new ArrayList<>();
        allUsers = tourGuideService.getAllUsers();
        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

        allUsers.forEach(u -> rewardsService.calculateRewards(u));

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
