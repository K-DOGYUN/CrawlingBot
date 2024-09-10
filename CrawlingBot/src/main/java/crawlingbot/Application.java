package crawlingbot;

import crawlingbot.crawling.timer.CrawlingTimer;
import crawlingbot.discord.DiscordBot;
import crawlingbot.domain.WebpageConfigs;

public class Application {
	
	public static void main(String[] args) {
		DiscordBot discordBot = new DiscordBot();
		discordBot.buildingBot();
		
		WebpageConfigs.getInstance().loadConfigs();
		CrawlingTimer.timer().setTimer();
	}
}
