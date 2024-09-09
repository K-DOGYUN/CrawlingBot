package crawlingbot.discord.domain;

import org.apache.commons.lang3.StringUtils;

import crawlingbot.discord.commands.SlashCommandFunctions.BotOption;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Getter
@Setter
public class WebpageConfig {
	private String channelId;
	private String configName;
	private String webpageUrl;
	private boolean imgVisivility = false;
	private int crawlingCycle = 5;
	
	public boolean isSame(String channelId, String configName) {
		return StringUtils.equals(channelId, this.channelId) && StringUtils.equals(configName, this.configName);
	}
	
	public WebpageConfig setByDiscord(SlashCommandInteractionEvent event) {
		this.channelId = event.getChannelId();
		this.configName = event.getOption(BotOption.ADD_TARGET_NAME.getName(), this.configName, OptionMapping::getAsString);
		this.webpageUrl = event.getOption(BotOption.ADD_WEBPAGE_URL.getName(), this.webpageUrl, OptionMapping::getAsString);
		this.imgVisivility = event.getOption(BotOption.ADD_IMG_VISIVILITY.getName(), this.imgVisivility, OptionMapping::getAsBoolean);
		this.crawlingCycle = event.getOption(BotOption.ADD_CRAWLING_CYCLE.getName(), this.crawlingCycle, OptionMapping::getAsInt);
		
		return this;
	}
	
	public String toString() {
		return 
				"\r\n Config name: " + configName
				+ "\r\n====<> Crawling Target Webpage Url: " + webpageUrl
				+ "\r\n====<> Image Visivility: " + imgVisivility
				+ "\r\n====<> Crawling Cycle: " + crawlingCycle + " second";
	}
}
