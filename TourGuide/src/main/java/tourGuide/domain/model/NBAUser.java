package tourGuide.domain.model;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBAUser {
    private Location location;

    public NBAUser(Location location) {
        this.location = location;
    }
}
