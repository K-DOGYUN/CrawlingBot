package crawlingbot.crawling.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import crawlingbot.crawling.dcinside.CrawlingGallery;
import crawlingbot.discord.DiscordBot;
import crawlingbot.domain.WebpageConfigs;
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
        Runnable task = () -> {
            log.info("Crawl task started");
            crawl(30);
            log.info("Crawl task finished");
        };

        log.info("Timer Setting");
        
        // Test log messages
        scheduler.scheduleAtFixedRate(() -> log.info("Scheduled every 1 second"), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    // Optionally, you can provide methods to manage scheduler shutdown
    public void shutdown() {
        log.info("Shutting down scheduler");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    
    private void crawl(int cycle) {
        log.info("Now crawling with cycle: " + cycle);
        DiscordBot.getDiscordBot().sendMessage("1234820747302539284", "test");
//        WebpageConfigs.getInstance().getConfigByCycle(cycle).forEach(config -> {
//            String content = CrawlingGallery.crawling(config);
//            DiscordBot.getDiscordBot().sendMessage(config.getChannelId(), content);
//        });
    }
}