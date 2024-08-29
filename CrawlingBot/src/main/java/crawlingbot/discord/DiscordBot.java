package crawlingbot.discord;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawlingbot.discord.domain.ChannelDto;
import crawlingbot.discord.domain.GuildDto;
import crawlingbot.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Slf4j
public class DiscordBot extends ListenerAdapter{
	private static String botToken = new String(Base64.decodeBase64(PropertyUtil.getProps("discord.botToken")));
	
	private static List<GuildDto> accessGuilds = new ArrayList<>();
	
	public void buildingBot() {
		JDABuilder
			.createDefault(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
			.addEventListeners(new DiscordBot())
			.build();
	}
	
	/**
	 * 봇 실행시에 호출
	 */
	@Override 
	public void onReady(ReadyEvent event) {
		log.info("====<> BOT READY");
		

		event.getJDA().getGuilds().forEach(guild -> {
			log.info("========<> guild: {}", guild.getName());
			
			GuildDto guildInfo = new GuildDto();
			guildInfo.setId(guild.getId());
			guildInfo.setName(guild.getName());
			
			ChannelDto channelInfo = new ChannelDto();

			guild.getTextChannels().forEach(textChannel -> {
				log.info("============<> textChannel: {}", textChannel.getName());
				
				channelInfo.setId(textChannel.getId());
				channelInfo.setName(textChannel.getName());
				guildInfo.getChannels().add(channelInfo);
			});
			
			accessGuilds.add(guildInfo);
		});
		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			mapper.writeValue(new File("src/main/resources/guidls.json"), accessGuilds);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 채널에서 메시지를 보냈을때 
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		log.info("====<> MESSAGE RECEIVED");
		
		log.info("========<> textChannel: {}", event.getChannel().getName());
		log.info("========<> author: {}", event.getAuthor().getName());
		log.info("========<> content: \n{}", event.getMessage().getContentDisplay());
	}
}
