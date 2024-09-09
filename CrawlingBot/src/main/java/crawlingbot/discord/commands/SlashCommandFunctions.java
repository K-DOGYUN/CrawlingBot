package crawlingbot.discord.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawlingbot.discord.DiscordBot;
import crawlingbot.discord.domain.WebpageConfig;
import crawlingbot.discord.exception.DiscordCommandException;
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
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

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
			Arrays.stream(BotSubCommand.values()).filter(bsc -> StringUtils.equals(bsc.getParent(), bc.getName()))
			.forEach(bsc -> {
				SubcommandData subcommandData = bsc.getCommand();
				
				/*Set Options for Sub Command*/
				Arrays.stream(BotOption.values())
				.filter(bo -> StringUtils.equals(bo.getParent(), bsc.getName()))
				.forEach(bo -> subcommandData.addOptions(bo.getOption()));
				
				commandData.addSubcommands(subcommandData);
			});
			
			/*Set Options for Slash Command*/
			Arrays.stream(BotOption.values())
			.filter(bo -> StringUtils.equals(bo.getParent(), bc.getName()))
			.forEach(bo -> commandData.addOptions(bo.getOption()));
			
			commands.addCommands(commandData);
		});
		
		commands.queue();
	}
	
	public void addTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Add Target Webpage");
		
		/*A configuration with this name already exists.*/
		if (DiscordBot.webpageConfigs.stream().anyMatch(wc -> wc.isSame(event.getChannelId(),
				event.getOption(BotOption.ADD_TARGET_NAME.getName(), OptionMapping::getAsString)))) {
			throw new DiscordCommandException("A configuration with this name already exists.\n", event);
		}
		
		/*add new config*/
		WebpageConfig config = new WebpageConfig().setByDiscord(event);
		DiscordBot.webpageConfigs.add(config);
		log.debug("====<> {}", config.toString());
		
		event.reply("")
		.addContent(config.toString())
		.addContent("\r\nAdd WebpageConfig success")
		.queue();

		saveWebpageConfigs();
	}
	
	public void editTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Edit Target Webpage");
		
		/*No configuration with such a name exists.*/
		if (!DiscordBot.webpageConfigs.stream().anyMatch(wc -> wc.isSame(event.getChannelId(),
				event.getOption(BotOption.ADD_TARGET_NAME.getName(), OptionMapping::getAsString))))
			throw new DiscordCommandException("No configuration with such a name exists.\n", event);
		
		/*edit*/
		DiscordBot.webpageConfigs.stream()
		.filter(wc -> wc.isSame(event.getChannelId(), event.getOption(BotOption.ADD_TARGET_NAME.getName(), OptionMapping::getAsString)))
		.forEach(wc -> {
			wc.setByDiscord(event);
			
			event.reply("")
			.addContent(wc.toString())
			.addContent("Edit WebpageConfig success")
			.queue();
		});
		
		saveWebpageConfigs();
		
	}
	
	public void deleteTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Delete Target Webpage");
		
		ReplyCallbackAction reply = event.reply("");
		
		if (!DiscordBot.webpageConfigs.removeIf(wc -> wc.isSame(event.getChannelId(),
				event.getOption(BotOption.DEL_TARGET_NAME.getName(), OptionMapping::getAsString))))
			throw new DiscordCommandException("No configuration with such a name exists.\n", event);
		
		saveWebpageConfigs();
		
		reply.addContent("Delete WebpageConfig success").queue();
	}
	
	public void lookUpTargetWebpage(SlashCommandInteractionEvent event) {
		ReplyCallbackAction reply = event.reply("Registered config list\r\n");
		
		DiscordBot.webpageConfigs.stream()
		.filter(wc -> StringUtils.equals(event.getChannelId(), wc.getChannelId()))
		.forEach(wc -> reply.addContent(wc.toString()));
		
		reply.queue();
	}
	
	private void saveWebpageConfigs() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			mapper.writeValue(new File("src/main/resources/webpageConfigs.json"), DiscordBot.webpageConfigs);
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
		ADD_IMG_VISIVILITY("img-visivility", "Defalut value is false", OptionType.BOOLEAN, "w-add", false),
		ADD_CRAWLING_CYCLE("crawling-cycle", "Default value is 5 sec", OptionType.INTEGER, "w-add", false),
		
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
}
