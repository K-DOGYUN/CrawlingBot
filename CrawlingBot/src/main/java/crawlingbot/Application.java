package crawlingbot;

import crawlingbot.crawling.timer.CrawlingTimer;
import crawlingbot.discord.DiscordBot;
import crawlingbot.domain.WebpageConfigs;

public class Application {
	
	public static void main(String[] args) {
		/*boot bot*/
		DiscordBot.getDiscordBot().buildingBot();
		/*loading crawling webpage configs*/
		WebpageConfigs.getInstance().loadConfigs();
	}
}
