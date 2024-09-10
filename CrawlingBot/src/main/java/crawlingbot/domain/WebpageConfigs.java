package crawlingbot.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawlingbot.discord.commands.SlashCommandFunctions.BotOption;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Slf4j
public class WebpageConfigs {
	private static List<WebpageConfig> webpageConfigs = new ArrayList<>();
	
    private WebpageConfigs() {
    }

    private static class InitConfigs {
        private static final WebpageConfigs CONFIGS = new WebpageConfigs();
    }

    public static WebpageConfigs getInstance() {
        return InitConfigs.CONFIGS;
    }
    
    public boolean isContain(String channelId, String name) {
    	return webpageConfigs.stream().anyMatch(wc -> wc.isSame(channelId, name));
    }

    public boolean addConfig(WebpageConfig config) {
		return isContain(config.getChannelId(), config.getConfigName()) ? false : webpageConfigs.add(config);
    }
    
    public WebpageConfig getMatchedConfig(String channelId, String name) {
		return isContain(channelId, name)
				? webpageConfigs.stream().filter(wc -> wc.isSame(channelId, name)).findAny().get()
				: null;
    }
    
	public boolean editConfig(WebpageConfig config) {
		if (!isContain(config.getChannelId(), config.getConfigName())) {
			return false;
		} else {
			webpageConfigs.stream().filter(wc -> wc.isSame(config.getChannelId(), config.getConfigName())).findAny().get().editByConfig(config);
			return true;
		}
	}
    
    public boolean editConfigByDiscordEvent(SlashCommandInteractionEvent event) {
    	String channelId = event.getChannelId();
    	String name = event.getOption(BotOption.EDIT_TARGET_NAME.getName(), OptionMapping::getAsString);
    	if (!isContain(channelId, name)) {
    		return false;
    	} else {
    		webpageConfigs.stream().filter(wc -> wc.isSame(channelId, name)).findAny().get().setByDiscord(event);
    		return true;
    	}
    }
    
    public boolean deleteConfig(String channelId, String name) {
    	return webpageConfigs.removeIf(wc -> wc.isSame(channelId, name));
    }
    
    public String toStringForLookUp() {
    	StringBuilder sb = new StringBuilder();
    	webpageConfigs.forEach(wc -> sb.append(wc.toString()));
    	return sb.toString();
    }
    
    public List<WebpageConfig> getConfigs() {
    	return webpageConfigs;
    }
    
    public void loadConfigs() {
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
    
    public void saveConfigs() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			mapper.writeValue(new File("src/main/resources/webpageConfigs.json"), webpageConfigs);
		} catch (IOException e) {
			log.error(e.toString());
		}
    }
}
