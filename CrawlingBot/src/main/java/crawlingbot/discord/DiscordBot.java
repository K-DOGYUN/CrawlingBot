package crawlingbot.discord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import crawlingbot.discord.commands.SlashCommandFunctions;
import crawlingbot.discord.domain.GuildDto;
import crawlingbot.discord.domain.WebpageConfig;
import crawlingbot.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
public class DiscordBot extends ListenerAdapter {
	private static String botToken = new String(Base64.decodeBase64(PropertyUtil.getProps("discord.botToken")));

	private static List<GuildDto> accessGuildsInformation = new ArrayList<>();
	
	public static List<GuildDto> getGuildsInformation() {
		return accessGuildsInformation;
	}
	
	public static List<WebpageConfig> webpageConfigs = new ArrayList<>();

	public void buildingBot() {
		JDABuilder builder = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT);
		builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
		builder.addEventListeners(new DiscordBot()).build();
	}

	@Override
	public void onReady(ReadyEvent event) {
		log.info("====<> BOT READY");
		/* Slash Command Initialization */
		SlashCommandFunctions botCommands = new SlashCommandFunctions();
		botCommands.initCommands(event.getJDA().updateCommands());

		/* Read Crawling Target Webpage Configs */
		ObjectMapper mapper = new ObjectMapper();
		try {
			webpageConfigs = mapper.readValue(
					new File("src/main/resources/webpageConfigs.json"),
					new TypeReference<List<WebpageConfig>>() {});
			log.info("====<> Read WebpageConfigs Success");
		} catch (IOException e) {
			log.error("====<> Read WebpageConfigs Failed!");
			log.error(e.toString());
		}
	}

	/**
	 * onMessageReceived
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		/* ignore if bot */
		if (event.getAuthor().isBot())
			return;

		log.info("====<> MESSAGE RECEIVED");

		log.debug("========<> textChannel: {}", event.getChannel().getName());
		log.debug("========<> author: {}", event.getAuthor().getName());
		log.debug("========<> content: \n{}", event.getMessage().getContentDisplay());
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		SlashCommandFunctions command = new SlashCommandFunctions();
		
		switch (event.getSubcommandName()) {
		case "w-add":
			command.addTargetWebpage(event);
			break;
		case "w-edit":
			command.editTargetWebpage(event);
			break;
		case "w-del":
			command.deleteTargetWebpage(event);
			break;
		case "w-lu":
			command.lookUpTargetWebpage(event);
			break;
		default:
			break;
		}
	}
}
