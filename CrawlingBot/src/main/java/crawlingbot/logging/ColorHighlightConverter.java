package crawlingbot.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class ColorHighlightConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		return switch (event.getLevel().toString()) {
			case "INFO" -> ANSIConstants.GREEN_FG; 
			case "ERROR" -> ANSIConstants.RED_FG; 
			default -> ANSIConstants.DEFAULT_FG;
		};
	}

}
