package crawlingbot.discord.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawlingbot.discord.exception.DiscordCommandException;
import crawlingbot.domain.WebpageConfig;
import crawlingbot.domain.WebpageConfigs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

@Slf4j
public class SlashCommandFunctions {
	
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
		/*Set Bot Slash Command*/
		Arrays.stream(BotSlashCommand.values())
		.forEach(bc -> {
			SlashCommandData commandData = bc.getCommand();
			
			/*Set Bot Sub Command*/
			Arrays.stream(BotSubCommand.values())
			.filter(bsc -> StringUtils.equals(bsc.getParent(), bc.getName()))
			.forEach(bsc -> {
				SubcommandData subcommandData = bsc.getCommand();
				
				/*Set Options for Sub Command*/
				Arrays.stream(BotOption.values())
				.filter(bo -> StringUtils.equals(bo.getParent(), bsc.getName()))
				.forEach(bo -> {
					OptionData optionData = bo.getOption();
					
					/*Set Option Choices*/
					Arrays.stream(OptionChoice.values())
					.filter(ch -> StringUtils.equals(ch.getParent(), bo.getName()))
					.forEach(ch -> optionData.addChoice(ch.getName(), ch.getValue()));
					
					subcommandData.addOptions(optionData);
				});
				
				commandData.addSubcommands(subcommandData);
			});
			
			/*Set Options for Slash Command*/
			Arrays.stream(BotOption.values())
			.filter(bo -> StringUtils.equals(bo.getParent(), bc.getName()))
			.forEach(bo -> {
				OptionData optionData = bo.getOption();
				
				/*Set Option Choices*/
				Arrays.stream(OptionChoice.values())
				.filter(ch -> StringUtils.equals(ch.getParent(), bo.getName()))
				.forEach(ch -> optionData.addChoice(ch.getName(), ch.getValue()));
				
				commandData.addOptions(optionData);
			});
			
			commands.addCommands(commandData);
		});
		
		commands.queue();
	}
	
	public void addTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Add Target Webpage");
		
		if (!WebpageConfigs.getInstance().addConfig(new WebpageConfig().setByDiscord(event)))
			throw new DiscordCommandException("A configuration with this name already exists.\r\n", event);
		
		event.reply("Add WebpageConfig success\r\n").queue();
		
		WebpageConfigs.getInstance().saveConfigs();
	}
	
	public void editTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Edit Target Webpage");
		
		if (!WebpageConfigs.getInstance().editConfigByDiscordEvent(event))
			throw new DiscordCommandException("No configuration with such a name exists.\n", event);
		
		event.reply("Edit WebpageConfig success\r\n").queue();
		
		WebpageConfigs.getInstance().saveConfigs();
	}
	
	public void deleteTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Delete Target Webpage");
		
		if (!WebpageConfigs.getInstance().deleteConfig(event.getChannelId(),
				event.getOption(BotOption.DEL_TARGET_NAME.getName(), OptionMapping::getAsString)))
			throw new DiscordCommandException("No configuration with such a name exists.\n", event);
		
		event.reply("Delete WebpageConfig success\r\n").queue();
		
		saveWebpageConfigs();
	}
	
	public void lookUpTargetWebpage(SlashCommandInteractionEvent event) {
		event.reply("Crawling Target Webpage Config List")
		.addContent(WebpageConfigs.getInstance().toStringForLookUp())
		.queue();
	}
	
	private void saveWebpageConfigs() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			mapper.writeValue(new File("src/main/resources/webpageConfigs.json"), WebpageConfigs.getInstance().getConfigs());
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	@Getter
	public static enum BotSlashCommand {
		CRAWLING_SETTING("cw", "Crawling Setting");
		
		private final String name;
		private final String description;
		
		BotSlashCommand(
				String name, 
				String description
				) {
			this.name = name;
			this.description = description;
		}
		
		public SlashCommandData getCommand() {
			return Commands.slash(name, description);
		}
	}
	
	@Getter
	public enum BotSubCommand {
		LOOKUP_TARGET_WEBPAGE("w-lu", "Look Up Target Webpage List", "cw"),
		
		ADD_TARGET_WEBPAGE("w-add", "Add Target Webpage", "cw"),
		EDIT_TARGET_WEBPAGE("w-edit", "Edit Target Webpage", "cw"),
		DELETE_TARGET_WEBPAGE("w-del", "Delete Target Webpage", "cw");
		
		private final String name;
		private final String description;
		private final String parent;
		
		BotSubCommand(
				String name, 
				String description, 
				String parent
				) {
			this.name = name;
			this.description = description;
			this.parent = parent;
		}
		
		public SubcommandData getCommand() {
			return new SubcommandData(name, description);
		}
	}
	
	@Getter
	public enum BotOption {
		ADD_TARGET_NAME("target-name", "Duplicate entry not allowed", OptionType.STRING, "w-add", true),
		ADD_WEBPAGE_URL("webpage-url", "Webpage Url", OptionType.STRING, "w-add", true),
		ADD_IMG_VISIBILITY("img-visibility", "Defalut value is false", OptionType.BOOLEAN, "w-add", false),
		ADD_CRAWLING_CYCLE("crawling-cycle", "Default value is 5 sec", OptionType.INTEGER, "w-add", false),

		EDIT_TARGET_NAME("target-name", "You can see target list by using '/cw lu'", OptionType.STRING, "w-edit", true),
		EDIT_WEBPAGE_URL("webpage-url", "Webpage Url", OptionType.STRING, "w-edit", false),
		EDIT_IMG_VISIBILITY("img-visibility", "Img Visibility", OptionType.BOOLEAN, "w-edit", false),
		EDIT_CRAWLING_CYCLE("crawling-cycle", "Input in seconds", OptionType.INTEGER, "w-edit", false),

		DEL_TARGET_NAME("target-name", "You can see target list by using '/cw lu'", OptionType.STRING, "w-del", true);
		
		private final String name;
		private final String description;
		private final OptionType optionType;
		private final String parent;
		private final boolean required;
		
		BotOption(
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
		
		public OptionData getOption() {
			return new OptionData(optionType, name, description, required);
		}
	}
	
	@Getter
	public enum OptionChoice {
		CYCLE_1SEC("1 sec", 1, "crawling-cycle"),
		CYCLE_5SEC("5 sec", 5, "crawling-cycle"),
		CYCLE_30SEC("30 sec", 30, "crawling-cycle"),
		CYCLE_60SEC("60 sec", 60, "crawling-cycle");
		
		private final String name;
		private final int value;
		private final String parent;
		
		OptionChoice(
				String name, 
				int value,
				String parent
				) {
			this.name = name;
			this.value = value;
			this.parent = parent;
		}
	}
}
