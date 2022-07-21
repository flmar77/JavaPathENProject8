package tourGuide.web;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.domain.model.NearByAttractions;
import tourGuide.domain.model.User;
import tourGuide.domain.model.UserLocations;
import tourGuide.domain.model.UserPreferences;
import tourGuide.domain.service.TourGuideService;
import tripPricer.Provider;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
public class TourGuideController {

    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(tourGuideService.getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearbyAttractions")
    public NearByAttractions getNearbyAttractions(@RequestParam String userName) {
        return tourGuideService.getNearByAttractions(userName);
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(tourGuideService.getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public List<UserLocations> getAllCurrentLocations() {
        return tourGuideService.getAllCurrentLocations();
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(tourGuideService.getUser(userName));
        return JsonStream.serialize(providers);
    }

    @PutMapping("/userPreferences/{userName}")
    public ResponseEntity<?> putUserPreferences(@PathVariable String userName,
                                                @RequestBody UserPreferences userPreferences) {

        log.debug("request for set userPreferences of userName : {}", userName);

        try {
            User userSaved = tourGuideService.updateUserPreferences(userName, userPreferences);
            return ResponseEntity.status(HttpStatus.OK).body(userSaved);
        } catch (NoSuchElementException e) {
            String logAndBodyMessage = "error while putting user because missing user with userName=" + userName;
            log.error(logAndBodyMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(logAndBodyMessage);
        }
    }
}