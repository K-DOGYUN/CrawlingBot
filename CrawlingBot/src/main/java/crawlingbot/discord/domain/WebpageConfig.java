package crawlingbot.discord.domain;


import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class WebpageConfig {
	private String channelId;
	private String configName;
	private String webpageUrl;
	private boolean imgVisivility = false;
	private int crawlingCycle = 5;
	
	public String[] getPk() {
		String[] pk = {this.channelId, this.configName};
		return pk;
	}
	
	public boolean isSame(String channelId, String configName) {
		return StringUtils.equals(channelId, this.channelId) && StringUtils.equals(configName, this.configName);
	}
}
