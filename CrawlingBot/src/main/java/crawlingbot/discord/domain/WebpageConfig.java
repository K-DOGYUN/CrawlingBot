package crawlingbot.discord.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebpageConfig {
	private String configName;
	private String webpageUrl;
	private String channelId;
	private boolean imgVisivility;
	private int crawlingCycle;
}
