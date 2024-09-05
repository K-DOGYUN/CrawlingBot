package crawlingbot.discord.commands;

import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import crawlingbot.discord.DiscordBot;
import crawlingbot.discord.domain.WebpageConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@Slf4j
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
			
			/*find matched subcommands*/
			BotSubCommands[] botSubCommands = 
					Arrays.stream(BotSubCommands.values())
					.filter(e -> StringUtils.equals(e.getParent(), bc.getName()))
					.toArray(BotSubCommands[]::new);
			
			for (BotSubCommands bsc : botSubCommands) {
				SubcommandData subcommandData = new SubcommandData(bsc.getName(), bsc.getDescription());
				
				/*find matched options*/
				BotOptions[] botOptions = 
						Arrays.stream(BotOptions.values())
						.filter(e -> StringUtils.equals(e.getParent(), bsc.getName()))
						.toArray(BotOptions[]::new);
				
				for (BotOptions bo : botOptions) {
					subcommandData.addOption(bo.getOptionType(), bo.getName(), bo.getDescription(), bo.isRequired());
				}
				
				commandData.addSubcommands(subcommandData);
			}
			
			/*find matched options*/
			BotOptions[] botOptions = 
					Arrays.stream(BotOptions.values())
					.filter(e -> StringUtils.equals(e.getParent(), bc.getName()))
					.toArray(BotOptions[]::new);
			
			for (BotOptions bo : botOptions) {
				commandData.addOption(bo.getOptionType(), bo.getName(), bo.getDescription(), bo.isRequired());
			}
			
			commands.addCommands(commandData);
		}
		
		commands.queue();
	}
	
	public void addTargetWebpage(SlashCommandInteractionEvent event) {
		ReplyCallbackAction reply = event.reply("");
		
		WebpageConfig config = new WebpageConfig();
		
		config.setChannelId(event.getChannelId());
		config.setConfigName(event.getOption(BotOptions.ADD_TARGET_NAME.getName()).getAsString());
		config.setWebpageUrl(event.getOption(BotOptions.ADD_WEBPAGE_URL.getName()).getAsString());
		
		try {
			config.setCrawlingCycle(event.getOption(BotOptions.ADD_CRAWLING_CYCLE.getName()).getAsInt());
		} catch (NullPointerException e) {
			log.error(e.toString());
			config.setCrawlingCycle(5);
			reply.addContent("Crawling cycle setted by Default: 5 sec\n");
		}
		
		try {
			config.setImgVisivility(event.getOption(BotOptions.ADD_IMG_VISIVILITY.getName()).getAsBoolean());
		} catch (NullPointerException e) {
			log.error(e.toString());
			config.setImgVisivility(false);
			reply.addContent("Crawling cycle setted by Default: false\n");
		}
		
		DiscordBot.webpageConfigs.put(config.getConfigName(), config);
		
		reply.queue();
	}
	
	@Getter
	public static enum BotCommands {
		CRAWLING_SETTING("cw", "Crawling Setting");
		
		private final String name;
		private final String description;
		
		BotCommands(
				String name, 
				String description
				) {
			this.name = name;
			this.description = description;
		}
	}
	
	@Getter
	public enum BotSubCommands {
		LOOKUP_TARGET_WEBPAGE("w-lu", "Look Up Target Webpage List", "cw"),
		
		ADD_TARGET_WEBPAGE("w-add", "Add Target Webpage", "cw"),
		EDIT_TARGET_WEBPAGE("w-edit", "Edit Target Webpage", "cw"),
		DELETE_TARGET_WEBPAGE("w-del", "Delete Target Webpage", "cw");
		
		private final String name;
		private final String description;
		private final String parent;
		
		BotSubCommands(
				String name, 
				String description, 
				String parent
				) {
			this.name = name;
			this.description = description;
			this.parent = parent;
		}
	}
	
	@Getter
	public enum BotOptions {
		ADD_TARGET_NAME("target-name", "Duplicate entry not allowed", OptionType.STRING, "w-add", true),
		ADD_WEBPAGE_URL("webpage-url", "Webpage Url", OptionType.STRING, "w-add", true),
		ADD_IMG_VISIVILITY("img-visivility", "Img Visivility", OptionType.BOOLEAN, "w-add", false),
		ADD_CRAWLING_CYCLE("crawling-cycle", "Input in seconds", OptionType.INTEGER, "w-add", false),
		
		EDIT_TARGET_NAME("target-name", "You can see target list by using '/cw lu'", OptionType.STRING, "w-edit", true),
		EDIT_WEBPAGE_URL("webpage-url", "Webpage Url", OptionType.STRING, "w-edit", false),
		EDIT_IMG_VISIVILITY("img-visivility", "Img Visivility", OptionType.BOOLEAN, "w-edit", false),
		EDIT_CRAWLING_CYCLE("crawling-cycle", "Input in seconds", OptionType.INTEGER, "w-edit", false),

		DEL_TARGET_NAME("target-name", "You can see target list by using '/cw lu'", OptionType.STRING, "w-del", true);
		
		private final String name;
		private final String description;
		private final OptionType optionType;
		private final String parent;
		private final boolean required;
		
		BotOptions(
				String name, 
				String description,
				OptionType optionType,
				String parent,
				boolean required
				) {
			this.name = name;
			this.description = description;
			this.optionType = optionType;
			this.parent = parent;
			this.required = required;
		}
	}
}
