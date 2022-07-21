package tourGuide.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NearByAttractions {
    private NBAUser user;
    private List<NBAAttraction> attractions;
}
