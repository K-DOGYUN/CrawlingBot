package crawlingbot.discord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawlingbot.discord.commands.BotCommandsUtil;
import crawlingbot.discord.domain.ChannelDto;
import crawlingbot.discord.domain.GuildDto;
import crawlingbot.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Slf4j
public class DiscordBot extends ListenerAdapter {
	private static String botToken = new String(Base64.decodeBase64(PropertyUtil.getProps("discord.botToken")));

	private static List<GuildDto> accessGuilds = new ArrayList<>();

	public void buildingBot() {
		JDABuilder builder = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.MESSAGE_CONTENT);

		builder.addEventListeners(new DiscordBot()).build();

	}

	/**
	 * 봇 실행시에 호출
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onReady(ReadyEvent event) {
		log.info("====<> BOT READY");
		/* slash commands initialization */
		BotCommandsUtil botCommands = new BotCommandsUtil();
		botCommands.initCommands(event.getJDA().updateCommands());
		/* slash commands initialization */
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			accessGuilds = mapper.readValue(new File("src/main/resources/guidls.json"), List.class);
			log.info("====<> READ CRAWLING DATA SUCCESS");
		} catch (IOException e) {
			log.error("====<> READ CRAWLING DATA FAILED!");
			log.error(e.toString());
		}

//		event.getJDA().getGuilds().forEach(guild -> {
//			log.info("========<> guild: {}", guild.getName());
//
//			/* guild info setting */
//			GuildDto guildInfo = new GuildDto();
//			guildInfo.setId(guild.getId());
//			guildInfo.setName(guild.getName());
//
//			guild.getTextChannels().forEach(textChannel -> {
//				log.info("============<> textChannel: {}", textChannel.getName());
//
//				ChannelDto channelInfo = new ChannelDto();
//				channelInfo.setId(textChannel.getId());
//				channelInfo.setName(textChannel.getName());
//				guildInfo.getChannels().add(channelInfo);
//			});
//
//			accessGuilds.add(guildInfo);
//		});
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//		try {
//			mapper.writeValue(new File("src/main/resources/guidls.json"), accessGuilds);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * onMessageReceived
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		log.info("====<> MESSAGE RECEIVED");

		log.debug("========<> textChannel: {}", event.getChannel().getName());
		log.debug("========<> author: {}", event.getAuthor().getName());
		log.debug("========<> content: \n{}", event.getMessage().getContentDisplay());

		/* ignore if bot */
		if (event.getAuthor().isBot())
			return;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		event.getCommandType();
		event.getName();
		event.getSubcommandName();
		event.getOption("option1");
		switch (event.getName()) {
		case "help":
			event.reply("now help command testing").queue();
			break;

		default:
			break;
		}
	}

}
