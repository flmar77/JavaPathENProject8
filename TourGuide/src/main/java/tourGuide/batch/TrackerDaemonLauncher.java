package tourGuide.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TrackerDaemonLauncher implements CommandLineRunner {

    private final TrackerDaemon trackerDaemon;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TrackerDaemonLauncher(TrackerDaemon trackerDaemon) {
        this.trackerDaemon = trackerDaemon;
    }

    @Override
    public void run(String... args) {
        executorService.submit(trackerDaemon);
    }
}
