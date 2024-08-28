package crawlingbot.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Slf4j
public class DiscordBot extends ListenerAdapter{
	private static String botToken = "MTIzMzc4MzU2NTMwMTU4Mzg4Mg.GKinrU.RzV0XgRu9DBj-6PcpXjLm73uY_vZt0qjo3GvOU";
	
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
			
			guild.getTextChannels().forEach(textChannel -> {
				log.info("============<> textChannel: {}", textChannel.getName());
			});
		});
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
