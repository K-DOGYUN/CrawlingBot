package crawlingbot.crawling.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrawlingTimer {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    private CrawlingTimer() {
    }

    private static class CreateTimer {
        private static final CrawlingTimer TIMER = new CrawlingTimer();
    }

    public static CrawlingTimer timer() {
        return CreateTimer.TIMER;
    }

    public void setTimer() {
    	scheduler.scheduleAtFixedRate(() -> log.info("1 sec"), 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(() -> log.info("5 sec"), 0, 5, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(() -> log.info("30 sec"), 0, 30, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(() -> log.info("1 min"), 0, 1, TimeUnit.MINUTES);
    }

    // Optionally, you can provide methods to manage scheduler shutdown
    public void shutdown() {
        scheduler.shutdown();
    }
}