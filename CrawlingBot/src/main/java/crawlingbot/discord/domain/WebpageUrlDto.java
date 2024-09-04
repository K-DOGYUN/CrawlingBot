package crawlingbot.discord.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebpageUrlDto {
	private String url;
	private String channelId;
	private String imgYn;
}
