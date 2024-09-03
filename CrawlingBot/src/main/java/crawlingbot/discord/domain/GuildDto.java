package crawlingbot.discord.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GuildDto {
	@NonNull
	private String id;
	@NonNull
	private String name;
	
	private List<ChannelDto> channels = new ArrayList<>();
}

