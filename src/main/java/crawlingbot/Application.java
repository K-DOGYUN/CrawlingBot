package crawlingbot;

import crawlingbot.crawling.timer.CrawlingTimer;
import crawlingbot.discord.DiscordBot;
import crawlingbot.domain.WebpageConfigs;

public class Application {
	
	public static void main(String[] args) throws InterruptedException {
		/*boot bot*/
		DiscordBot.getDiscordBot().buildingBot();
		/*loading crawling webpage configs*/
		WebpageConfigs.getInstance().loadConfigs();
		/*set crawling timers*/
		CrawlingTimer.timer().setTimer();
	}
}
