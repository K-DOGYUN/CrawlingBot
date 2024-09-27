package crawlingbot.discord;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import crawlingbot.crawling.timer.CrawlingTimer;
import crawlingbot.discord.commands.SlashCommandFunctions;
import crawlingbot.domain.WebpageConfigs;
import crawlingbot.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
public class DiscordBot extends ListenerAdapter {
	private static String botToken = new String(Base64.decodeBase64(PropertyUtil.getProps("discord.botToken")));
	private static JDA BOT;

	private DiscordBot() {
	}

	private static class InitBot {
		private static final DiscordBot DISCORD_BOT = new DiscordBot();
	}

	public static DiscordBot getDiscordBot() {
		return InitBot.DISCORD_BOT;
	}

	public void buildingBot() throws InterruptedException {
		if (StringUtils.isEmpty(botToken))
			throw new IllegalStateException("Bot token is empty");
		
		BOT = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
				.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
				.addEventListeners(getDiscordBot()).build().awaitReady();
	}

	public void sendMessage(String channelId, String content) {
		if (StringUtils.isAnyEmpty(channelId, content))
			throw new IllegalArgumentException("Channel ID and content must not be empty.");
		
		if (BOT.getTextChannelById(channelId) == null)
			throw new NullPointerException("Channel with ID " + channelId + " does not exist.");
		
		BOT.getTextChannelById(channelId).sendMessage(content).queue(
		        success -> log.info("Message sent to channel {}: {}", channelId, content),
		        failure -> log.error("Failed to send message to channel {}: {}", channelId, failure.getMessage())
	    );
	}
	
	public void sendMessage(String channelId, MessageEmbed embed) {
		if (ObjectUtils.isEmpty(channelId) || ObjectUtils.isEmpty(embed))
			throw new IllegalArgumentException("Channel ID and content must not be empty.");
		
		if (BOT.getTextChannelById(channelId) == null)
			throw new NullPointerException("Channel with ID " + channelId + " does not exist.");
		
		BOT.getTextChannelById(channelId).sendMessageEmbeds(embed).queue(
		        success -> log.info("Message sent to channel {}: {}", channelId, embed),
		        failure -> log.error("Failed to send message to channel {}: {}", channelId, failure.getMessage())
	    );
	}

	@Override
	public void onReady(ReadyEvent event) {
		log.info("====<> BOT READY");
		/* Slash Command Initialization */
		new SlashCommandFunctions().initCommands(event.getJDA().updateCommands());
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
		syncedSlashCommandEvent(event);
	}
	
	private synchronized void syncedSlashCommandEvent(SlashCommandInteractionEvent event) {
		if (event == null) 
			throw new NullPointerException("There is no SlashCommandInteractionEvent");
		
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
