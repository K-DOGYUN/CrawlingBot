package crawlingbot.crawling.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import crawlingbot.crawling.dcinside.CrawlingGallery;
import crawlingbot.discord.DiscordBot;
import crawlingbot.domain.WebpageConfig;
import crawlingbot.domain.WebpageConfigs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrawlingTimer {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    private CrawlingTimer() {
    }

    private static class CreateTimer {
        private static final CrawlingTimer TIMER = new CrawlingTimer();
    }

    public static CrawlingTimer timer() {
        return CreateTimer.TIMER;
    }

    public void setTimer() {
        log.info("Timer Setting");
        
        scheduler.scheduleAtFixedRate(() -> crawl(1), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> crawl(5), 0, 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> crawl(30), 0, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> crawl(60), 0, 60, TimeUnit.SECONDS);
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
//        log.info("Now crawling with cycle: " + cycle);
        for (WebpageConfig config  : WebpageConfigs.getInstance().getConfigByCycle(cycle)) {
        	try {
        		String content = CrawlingGallery.crawling(config);
        		if (StringUtils.isEmpty(content))
        			return;
        		log.info("content length: {}", content.length());
        		DiscordBot.getDiscordBot().sendMessage(config.getChannelId(), content);
			} catch (Exception e) {
				log.error("error to read webpage : {}, {}", config.getConfigName(), config.getWebpageUrl());
			}
		}
    }
}