package tourGuide.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TrackerRunner implements CommandLineRunner {
    private final Tracker tracker;

    public TrackerRunner(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void run(String... args) {
        tracker.startTracker();
    }
}
