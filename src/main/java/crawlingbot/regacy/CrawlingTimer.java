//package crawlingbot.regacy;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import crawling.CrawlingHellDivers2Gal;
//import discord.DiscordBot;
//import discord.SendMessage;
//import telegram.CallTelegramBot;
//
//public class CrawlingTimer {
//    public void run(DiscordBot discordBot) throws IOException, URISyntaxException {
//        Timer timer = new Timer();
//    	CrawlingHellDivers2Gal crawling = new CrawlingHellDivers2Gal();
//    	SendMessage sendMessage = new SendMessage();
//    	
//		// 1초 후에 시작하고, 매 5초마다 실행
//		timer.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				sendMessage.sendMessage(discordBot, crawling.crawling());
//			}
//		}, 5000, 5000); // 1000ms 지연 후 시작, 5000ms 간격으로 실행
//    }
//}