package crawlingbot.discord.domain;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto {
	@NonNull
	private String id;
	@NonNull
	private String name;

	private boolean useYn = false;
	
	private List<TargetWebPage> targets = new ArrayList<>();
	
	@Getter
	@Setter
	public class TargetWebPage {
		@NonNull
		private String url;
		@NotNull
		private String name;
	}
}
