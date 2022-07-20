package tourGuide.externalmodules;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@Configuration
public class ExternalModulesStarter {

    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

    @Bean
    public TripPricer getTripPricer() {
        return new TripPricer();
    }

}
