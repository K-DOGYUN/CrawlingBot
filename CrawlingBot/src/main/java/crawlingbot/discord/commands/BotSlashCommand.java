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
		log.info("====<> Add Target Webpage");
		
		ReplyCallbackAction reply = event.reply("");
		
		WebpageConfig config = new WebpageConfig();
		
		config.setChannelId(event.getChannelId());
		config.setConfigName(event.getOption(BotOptions.ADD_TARGET_NAME.getName(), OptionMapping::getAsString));
		config.setWebpageUrl(event.getOption(BotOptions.ADD_WEBPAGE_URL.getName(), OptionMapping::getAsString));
		config.setCrawlingCycle(event.getOption(BotOptions.ADD_CRAWLING_CYCLE.getName(), 5, OptionMapping::getAsInt));
		config.setImgVisivility(event.getOption(BotOptions.ADD_IMG_VISIVILITY.getName(), false, OptionMapping::getAsBoolean));
		
		/*A configuration with this name already exists.*/
		if (DiscordBot.webpageConfigs.stream().anyMatch(wc -> wc.isSame(config.getChannelId(), config.getConfigName())))
			throw new DiscordCommandException("A configuration with this name already exists.\n", event);
		
		/*add new config*/
		log.info("====<> {}", config.toString());
		DiscordBot.webpageConfigs.add(config);
		
		saveWebpageConfigs();
		
		reply.addContent("Add WebpageConfig success").queue();
	}
	
	public void editTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Edit Target Webpage");
		
		ReplyCallbackAction reply = event.reply("");
		
		/*No configuration with such a name exists.*/
		if (!DiscordBot.webpageConfigs.stream().anyMatch(wc -> wc.isSame(event.getChannelId(),
				event.getOption(BotOptions.ADD_TARGET_NAME.getName(), OptionMapping::getAsString))))
			throw new DiscordCommandException("No configuration with such a name exists.\n", event);
		
		/*edit*/
		DiscordBot.webpageConfigs.stream()
		.filter(wc -> wc.isSame(event.getChannelId(), event.getOption(BotOptions.ADD_TARGET_NAME.getName(), OptionMapping::getAsString)))
		.forEach(wc -> {
			wc.setWebpageUrl(event.getOption(BotOptions.EDIT_WEBPAGE_URL.getName(), wc.getWebpageUrl(), OptionMapping::getAsString));
			wc.setCrawlingCycle(event.getOption(BotOptions.EDIT_CRAWLING_CYCLE.getName(), wc.getCrawlingCycle(), OptionMapping::getAsInt));
			wc.setImgVisivility(event.getOption(BotOptions.EDIT_IMG_VISIVILITY.getName(), wc.isImgVisivility(), OptionMapping::getAsBoolean));
		});
		
		saveWebpageConfigs();
		
		reply.addContent("Edit WebpageConfig success").queue();
	}
	
	public void deleteTargetWebpage(SlashCommandInteractionEvent event) {
		log.info("====<> Delete Target Webpage");
		
		ReplyCallbackAction reply = event.reply("");
		
		if (!DiscordBot.webpageConfigs.removeIf(wc -> wc.isSame(event.getChannelId(),
				event.getOption(BotOptions.DEL_TARGET_NAME.getName(), OptionMapping::getAsString))))
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
