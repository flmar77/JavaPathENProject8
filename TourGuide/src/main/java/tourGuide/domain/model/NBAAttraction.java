package tourGuide.domain.model;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NBAAttraction implements Comparable<NBAAttraction> {
    private UUID id;
    private String name;
    private Location location;
    private double distance;
    private int rewardPoints;

    @Override
    public int compareTo(NBAAttraction nbaAttraction) {
        return (int) (this.distance - nbaAttraction.getDistance());
    }
}
