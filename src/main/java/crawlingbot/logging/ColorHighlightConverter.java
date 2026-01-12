package crawlingbot.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * A custom Logback converter that applies ANSI color highlighting to log messages based on their log level.
 * 
 * <p>This converter extends {@link CompositeConverter} and overrides the {@code transform} method to apply
 * different colors to log messages depending on the log level. For example, INFO level messages are displayed in green,
 * ERROR level messages are displayed in red, and other levels are displayed with the default terminal color.</p>
 * 
 * <p>To use this converter, define it in the Logback configuration file (logback.xml) with a pattern using the
 * conversion word associated with this converter (e.g., %highlight). Ensure that the ANSI escape codes are supported
 * by the console or log viewer.</p>
 * 
 * <p>Example configuration in logback.xml:</p>
 * <pre>
 * &lt;configuration&gt;
 *     &lt;conversionRule conversionWord="highlight" converterClass="com.example.ColorHighlightConverter"/&gt;
 *     &lt;appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender"&gt;
 *         &lt;encoder&gt;
 *             &lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss} [%thread] %highlight(%-5level) %logger{36} - %msg%n&lt;/pattern&gt;
 *         &lt;/encoder&gt;
 *     &lt;/appender&gt;
 * &lt;/configuration&gt;
 * </pre>
 * 
 * @see CompositeConverter
 * @see ILoggingEvent
 */
public class ColorHighlightConverter extends CompositeConverter<ILoggingEvent> {
	
    /**
     * Transforms the log message text by applying ANSI color codes based on the log level.
     *
     * <p>This method checks the log level of the event and applies a corresponding ANSI color code:
     * <ul>
     *     <li>{@code INFO} level logs are colored green.</li>
     *     <li>{@code ERROR} level logs are colored red.</li>
     *     <li>All other levels are displayed in the default terminal color.</li>
     * </ul>
     * </p>
     *
     * @param event The logging event containing the log level and other details.
     * @param text  The original log message text to be transformed.
     * @return The transformed text with ANSI color codes applied.
     */
	@Override
	protected String transform(ILoggingEvent event, String text) {
		// TODO Auto-generated method stub
		return switch (event.getLevel().toString()) {
			case "INFO" -> AnsiColor.apply(AnsiColor.FORE_GREEN, text); 
			case "WARN" -> AnsiColor.apply(AnsiColor.FORE_BRIGHT_RED, text); 
			case "ERROR" -> AnsiColor.apply(AnsiColor.FORE_RED, text); 
			default -> AnsiColor.apply(AnsiColor.RESET_FOREGROUND, text);
		};
	}
}
