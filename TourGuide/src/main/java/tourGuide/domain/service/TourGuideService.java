package tourGuide.domain.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.User;
import tourGuide.domain.model.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TourGuideService {
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer;
    private final TourGuideFakeRepo tourGuideFakeRepo;

    private static final String tripPricerApiKey = "test-server-api-key";

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, TripPricer tripPricer, TourGuideFakeRepo tourGuideFakeRepo) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        this.tripPricer = tripPricer;
        this.tourGuideFakeRepo = tourGuideFakeRepo;
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        return (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
    }

    public User getUser(String userName) {
        return tourGuideFakeRepo.getUser(userName);
    }

    public List<User> getAllUsers() {
        return tourGuideFakeRepo.getAllUsers();
    }

    public void addUser(User user) {
        tourGuideFakeRepo.addUser(user);
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }
        return nearbyAttractions;
    }
}
