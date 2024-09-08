package crawlingbot.discord.domain;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

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
	
	public String toString() {
		return 
				"\r\n Config name: " + configName
				+ "\r\n====<> Crawling Target Webpage Url: " + webpageUrl
				+ "\r\n====<> Image Visivility: " + imgVisivility
				+ "\r\n====<> Crawling Cycle: " + crawlingCycle + " second";
	}
}
