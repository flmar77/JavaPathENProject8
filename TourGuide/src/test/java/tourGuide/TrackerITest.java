package tourGuide;

import gpsUtil.GpsUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.batch.Tracker;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.service.RewardsService;
import tourGuide.domain.service.TourGuideService;
import tripPricer.TripPricer;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

public class TrackerITest {

    private final GpsUtil gpsUtil = new GpsUtil();
    private final TripPricer tripPricer = new TripPricer();
    private final RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private static final TourGuideFakeRepo tourGuideFakeRepo = new TourGuideFakeRepo();
    private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer, tourGuideFakeRepo);
    private final Tracker tracker = new Tracker(tourGuideService);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @BeforeClass
    public static void setUpAllTests() {
        Locale.setDefault(Locale.US);
        tourGuideFakeRepo.initializeInternalUsers(10);
    }

    @Test
    public void tracker() {
        assertTrue(0 < tracker.trackAllUsers());
    }

}
