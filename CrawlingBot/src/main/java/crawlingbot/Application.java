package crawlingbot;

import crawlingbot.discord.DiscordBot;

public class Application {

	public static void main(String[] args) {
		DiscordBot discordBot = new DiscordBot();
		discordBot.buildingBot();
	}
}
