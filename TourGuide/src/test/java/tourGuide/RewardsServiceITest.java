package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.User;
import tourGuide.domain.model.UserReward;
import tourGuide.domain.service.RewardsService;
import tourGuide.domain.service.TourGuideService;
import tripPricer.TripPricer;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RewardsServiceITest {

    private final GpsUtil gpsUtil = new GpsUtil();
    private final TripPricer tripPricer = new TripPricer();
    private final RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private final TourGuideFakeRepo tourGuideFakeRepo = new TourGuideFakeRepo();
    private final TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer, tourGuideFakeRepo);

    @Before
    public void setUpAllTests() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void userGetRewards() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtil.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        assertEquals(1, userRewards.size());
    }

    @Test
    public void isWithinAttractionProximity() {
        Attraction attraction = gpsUtil.getAttractions().get(0);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Ignore // Needs fixed - can throw ConcurrentModificationException
    @Test
    public void nearAllAttractions() {
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, tripPricer, tourGuideFakeRepo);

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

        assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

}
