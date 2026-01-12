package crawlingbot.discord.exception;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DiscordCommandException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    public DiscordCommandException(String message, SlashCommandInteractionEvent event) {
        super(message);
        sendToDiscord(message, event);
    }

    public DiscordCommandException(String message, Throwable cause, SlashCommandInteractionEvent event) {
        super(message, cause);
        sendToDiscord(message + " | Cause: " + cause.getMessage(), event);
    }

    private void sendToDiscord(String message, SlashCommandInteractionEvent event) {
    	event.reply(message).queue();
    }
}
