package crawlingbot.discord.commands;

import java.nio.channels.Channel;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import crawlingbot.discord.DiscordBot;
import crawlingbot.discord.domain.GuildDto;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class BotSlashCommand {
	
	/**
	 * Configures slash commands for the bot during initialization.
	 * 
	 * This method sets up the necessary slash commands and their corresponding handlers
	 * when the bot is started. This should be called once during the bot's startup process
	 * to ensure that all commands are properly registered and ready to use.
	 * 
	 * <p>Example usage:</p>
	 * <pre>
	 *     Bot bot = new Bot();
	 *     bot.initialize();
	 *     bot.setupSlashCommands();
	 * </pre>
	 * 
	 * @param commandName The name of the slash command to register.
	 * 
	 * @see #BotCommands
	 * @see #BotSubCommands
	 */
	public void initCommands(CommandListUpdateAction commands) {
		for (BotCommands bc : BotCommands.values()) {
			SlashCommandData commandData = Commands.slash(bc.getName(), bc.getDescription());
			
			for (BotSubCommands bsc : bc.getSubCommandList()) {
				SubcommandData subcommandData = new SubcommandData(bsc.getName(), bc.getDescription() + "-" + bsc.getDescription());
				
				if (!ObjectUtils.isEmpty(bsc.getOptionList())) {
					for (BotOptions op : bsc.getOptionList()) {
						subcommandData.addOption(OptionType.STRING, op.getName(), op.getDescription());
					}
				}
				
				commandData.addSubcommands(subcommandData);
			}
			
			commands.addCommands(commandData);
		}
		
		commands.queue();
	}
	
	public void slashCommandAction(SlashCommandInteractionEvent event) {
		switch (event.getName()) {
			case "cw":
				CrawlingAction(event);
				break;
	
			default:
				break;
		}
	}
	
	private void CrawlingAction(SlashCommandInteractionEvent event) {
		switch (event.getSubcommandName()) {
			case "tu":
				Guild guild = event.getGuild();
				MessageChannelUnion channel = event.getChannel();
				
//				if (DiscordBot.getGuildsInformation().stream().anyMatch(guild -> )) {
//					
//				} else {
//					
//				}
				break;
				
			default:
				break;
		}
//		DiscordBot.getGuildsInformation();
	}
	
	@Getter
	public static enum BotCommands {
		CRAWLING("cw", "Crawling setting", Arrays.asList(BotSubCommands.TARGET_WEBPAGE, BotSubCommands.IMAGE_PERMISSION));
		
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
		TARGET_WEBPAGE("wb", "Target Webpage", Arrays.asList(BotOptions.URL)),
		IMAGE_PERMISSION("ip", "Image permission", null);
		
		private final String name;
		private final String description;
		private final List<BotOptions> optionList;
		
		BotSubCommands(String name, String description, List<BotOptions> optionList) {
			this.name = name;
			this.description = description;
			this.optionList = optionList;
		}
	}
	
	@Getter
	public enum BotOptions {
		URL("url", "Target url"),
		IMAGE_PERMISSION("ip", "Image permission");
		
		private final String name;
		private final String description;
		
		BotOptions(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}
	
	
}
