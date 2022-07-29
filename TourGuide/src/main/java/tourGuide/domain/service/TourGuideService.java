package tourGuide.domain.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tourGuide.dal.TourGuideFakeRepo;
import tourGuide.domain.model.NBAUser;
import tourGuide.domain.model.NearByAttractions;
import tourGuide.domain.model.User;
import tourGuide.domain.model.UserLocations;
import tourGuide.domain.model.UserPreferences;
import tourGuide.domain.model.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TourGuideService {
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer;
    private final TourGuideFakeRepo tourGuideFakeRepo;
    private ExecutorService trackUserLocationThreadPool = Executors.newFixedThreadPool(100);

    private static final String tripPricerApiKey = "test-server-api-key";

    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, TripPricer tripPricer, TourGuideFakeRepo tourGuideFakeRepo) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        this.tripPricer = tripPricer;
        this.tourGuideFakeRepo = tourGuideFakeRepo;
    }

    public void trackUserLocationAwaitTerminationAfterShutdown() {
        trackUserLocationThreadPool.shutdown();
        try {
            if (!trackUserLocationThreadPool.awaitTermination(5, TimeUnit.MINUTES)) {
                trackUserLocationThreadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            trackUserLocationThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        trackUserLocationThreadPool = Executors.newFixedThreadPool(100);
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        try {
            return (user.getVisitedLocations().size() > 0) ?
                    user.getLastVisitedLocation() :
                    trackUserLocation(user).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey,
                user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(),
                cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public Future<VisitedLocation> trackUserLocation(User user) {
        return CompletableFuture.supplyAsync(() -> {
            VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
            user.addToVisitedLocations(visitedLocation);
            try {
                rewardsService.calculateRewards(user).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("trackUserLocation failed");
                throw new RuntimeException(e);
            }
            return visitedLocation;
        }, trackUserLocationThreadPool);
    }

    public NearByAttractions getNearByAttractions(String userName) {
        NearByAttractions nearbyAttractions = new NearByAttractions();
        User user = getUser(userName);
        Location userLocation = getUserLocation(user).location;

        nearbyAttractions.setUser(new NBAUser(userLocation));

        nearbyAttractions.setAttractions(rewardsService.getFiveNearestAttractions(user, userLocation));

        return nearbyAttractions;
    }

    public List<UserLocations> getAllCurrentLocations() {
        return getAllUsers().stream()
                .map(user -> {
                    UserLocations userLocations = new UserLocations();
                    userLocations.setUserId(user.getUserId());
                    userLocations.setLocations(user.getVisitedLocations().stream()
                            .map(visitedLocation -> visitedLocation.location)
                            .collect(Collectors.toList()));
                    return userLocations;
                })
                .collect(Collectors.toList());
    }

    public User updateUserPreferences(String userName, UserPreferences userPreferences) {
        User user = getUser(userName);

        if (null == user) {
            throw new NoSuchElementException();
        }

        return tourGuideFakeRepo.updateUserPreferences(user, userPreferences);
    }
}
