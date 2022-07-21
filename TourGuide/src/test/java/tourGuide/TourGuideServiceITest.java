package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.NearByAttractions;
import tourGuide.domain.model.User;
import tourGuide.domain.model.UserPreferences;
import tourGuide.domain.service.RewardsService;
import tourGuide.domain.service.TourGuideService;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TourGuideServiceITest {

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
    public void trackUser() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void addUser() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrievedUser = tourGuideService.getUser(user.getUserName());
        User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

        assertEquals(user, retrievedUser);
        assertEquals(user2, retrievedUser2);
    }

    @Test
    public void getAllUsers() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void getNearbyAttractions() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideFakeRepo.addUser(user);

        NearByAttractions nearByAttractions = tourGuideService.getNearByAttractions(user.getUserName());

        assertEquals(5, nearByAttractions.getAttractions().size());
    }

    @Test
    public void getTripDeals() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        assertEquals(5, providers.size());
    }

    @Test
    public void getAllCurrentLocations() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "getAllCurrentLocations", "000", "getAllCurrentLocations@tourGuide.com");
        user.setVisitedLocations(Collections.singletonList(new VisitedLocation(userId, new Location(1, 2), new Date())));
        tourGuideFakeRepo.addUser(user);

        assertNotEquals(null, tourGuideService.getAllCurrentLocations().get(0));
    }

    @Test
    public void updateUserPreferences() {
        User user = new User(UUID.randomUUID(), "updateUserPreferences", "000", "updateUserPreferences@tourGuide.com");
        tourGuideFakeRepo.addUser(user);

        assertNotEquals(null, tourGuideService.updateUserPreferences(user.getUserName(), new UserPreferences()));
    }

    @Test(expected = NoSuchElementException.class)
    public void updateUserPreferencesThrow() {
        tourGuideService.updateUserPreferences("prout", new UserPreferences());
    }
}
