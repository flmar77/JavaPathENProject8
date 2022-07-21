package tourGuide.domain.model;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserLocations {
    private UUID userId;
    private List<Location> locations;
}
