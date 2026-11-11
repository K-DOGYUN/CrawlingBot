package crawlingbot.domain;

import java.util.ArrayList;
import java.util.List;

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
	private boolean imgVisibility = false;
	private int crawlingCycle = 5;
	private List<String> latestCrawledUrls = new ArrayList<>();

	public boolean isSame(String channelId, String configName) {
		return StringUtils.equals(channelId, this.channelId) && StringUtils.equals(configName, this.configName);
	}

	public WebpageConfig setByDiscord(SlashCommandInteractionEvent event) {
		this.channelId = event.getChannelId();
		this.configName = event.getOption(BotOption.ADD_TARGET_NAME.getName(), this.configName, OptionMapping::getAsString);
		this.webpageUrl = event.getOption(BotOption.ADD_WEBPAGE_URL.getName(), this.webpageUrl, OptionMapping::getAsString);
		this.imgVisibility = event.getOption(BotOption.ADD_IMG_VISIBILITY.getName(), this.imgVisibility, OptionMapping::getAsBoolean);
		this.crawlingCycle = event.getOption(BotOption.ADD_CRAWLING_CYCLE.getName(), this.crawlingCycle, OptionMapping::getAsInt);

		return this;
	}

	public String toString() {
		return
				"\r\n Config name: " + configName
				+ "\r\n====<> Crawling Target Webpage Url: " + webpageUrl
				+ "\r\n====<> Image Visivility: " + imgVisibility
				+ "\r\n====<> Crawling Cycle: " + crawlingCycle + " second";
	}

	public WebpageConfig editByConfig(WebpageConfig config) {
		this.webpageUrl = config.getWebpageUrl();
		this.imgVisibility = config.isImgVisibility();
		this.crawlingCycle = config.getCrawlingCycle();

		return this;
	}
	
	/**
	 * 크롤링 봇에서 확인한 최근 URL을 추가하는 메서드 <br/>
	 * 크롤링 설정 하나당 최대 5개까지 저장한다
	 * @param latestCrawledUrl
	 * @return WebpageConfig
	 */
	public WebpageConfig addLatestCrawledUrl(String latestCrawledUrl) {
		if (!isLatestCrawledUrl(latestCrawledUrl)) latestCrawledUrls.add(0, latestCrawledUrl);
		if (latestCrawledUrls.size() > 5) latestCrawledUrls.subList(5, latestCrawledUrls.size()).clear();
		return this;
	}
	
	/**
	 * 입력된 URL 최근 크롤링된 게시글의 URL중 하나인지 판단하는 메서드
	 * @param latestCrawledUrl
	 * @return boolean
	 */
	public boolean isLatestCrawledUrl(String latestCrawledUrl) {
		return latestCrawledUrls.contains(latestCrawledUrl);
	}
}
