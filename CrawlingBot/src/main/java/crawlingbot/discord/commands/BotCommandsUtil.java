package crawlingbot.discord.commands;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class BotCommandsUtil {
	
	public void initCommands(CommandListUpdateAction commands) {
		/*add bot commands*/
		for (BotCommands bc : BotCommands.values()) {
			SlashCommandData commandData = Commands.slash(bc.getName(), bc.getDescription());
			
			/*add sub commands*/
			for (BotSubCommands bsc : bc.getSubCommandList()) {
				commandData.addSubcommands(
						new SubcommandData(bsc.getName(), bc.getDescription() + "-" + bsc.getDescription())
						);
			}
			
			commands.addCommands(commandData);
		}
		
		commands.queue();
	}
	
	@Getter
	public enum BotCommands {
		CRAWLING("cw", "Crawling setting", Arrays.asList(BotSubCommands.TARGET_URL, BotSubCommands.IMAGE_PERMISSION));
		
		private final String name;
		private final String description;
		private final List<BotSubCommands> subCommandList;
		
		BotCommands(String name, String description, List<BotSubCommands> subCommandList) {
			this.name = name;
			this.description = description;
			this.subCommandList = subCommandList;
		}
	}
	
	@Getter
	public enum BotSubCommands {
		TARGET_URL("tu", "Target url"),
		IMAGE_PERMISSION("ip", "Image permission");
		
		private final String name;
		private final String description;
		
		BotSubCommands(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}
}
