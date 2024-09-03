package crawlingbot.logging;

/**
 * Enum representing ANSI color codes for terminal text formatting.
 * 
 * This enum provides constants for various foreground and background colors,
 * text styles, and methods to apply these colors and styles to text. It supports
 * standard colors, bright colors, extended colors (256-color mode), and true color (24-bit RGB).
 * 
 * <p>Usage Example:</p>
 * <pre>
 *     String redText = AnsiColor.FORE_RED.apply("This is red text.");
 *     String greenTextOnBlack = AnsiColor.apply256Foreground(82, "This is green text on a black background.");
 *     String blueTextWithBold = AnsiColor.apply(AnsiColor.FORE_BLUE, AnsiColor.BOLD, "This is blue text with bold style.");
 * </pre>
 */
public enum AnsiColor {
    /**
     * Standard foreground colors.
     */
    FORE_BLACK("30"),
    FORE_RED("31"),
    FORE_GREEN("32"),
    FORE_YELLOW("33"),
    FORE_BLUE("34"),
    FORE_MAGENTA("35"),
    FORE_CYAN("36"),
    FORE_WHITE("37"),

    /**
     * Bright foreground colors.
     */
    FORE_BRIGHT_BLACK("90"),
    FORE_BRIGHT_RED("91"),
    FORE_BRIGHT_GREEN("92"),
    FORE_BRIGHT_YELLOW("93"),
    FORE_BRIGHT_BLUE("94"),
    FORE_BRIGHT_MAGENTA("95"),
    FORE_BRIGHT_CYAN("96"),
    FORE_BRIGHT_WHITE("97"),

    /**
     * Standard background colors.
     */
    BACK_BLACK("40"),
    BACK_RED("41"),
    BACK_GREEN("42"),
    BACK_YELLOW("43"),
    BACK_BLUE("44"),
    BACK_MAGENTA("45"),
    BACK_CYAN("46"),
    BACK_WHITE("47"),

    /**
     * Bright background colors.
     */
    BACK_BRIGHT_BLACK("100"),
    BACK_BRIGHT_RED("101"),
    BACK_BRIGHT_GREEN("102"),
    BACK_BRIGHT_YELLOW("103"),
    BACK_BRIGHT_BLUE("104"),
    BACK_BRIGHT_MAGENTA("105"),
    BACK_BRIGHT_CYAN("106"),
    BACK_BRIGHT_WHITE("107"),

    /**
     * Extended foreground colors (256-color mode).
     */
    FORE_EXTENDED("38;5;"),

    /**
     * Extended background colors (256-color mode).
     */
    BACK_EXTENDED("48;5;"),

    /**
     * True color (24-bit RGB) foreground.
     */
    FORE_RGB("38;2;"),

    /**
     * True color (24-bit RGB) background.
     */
    BACK_RGB("48;2;"),

    /**
     * Text styles.
     */
    RESET("0"),
    BOLD("1"),
    DIM("2"),
    ITALIC("3"),
    UNDERLINE("4"),
    BLINK("5"),
    REVERSE("7"),
    HIDDEN("8"),

    /**
     * Reset foreground and background colors.
     */
    RESET_FOREGROUND("39"),
    RESET_BACKGROUND("49");

    private final String code;

    /**
     * Constructs an {@code AnsiColor} with the specified ANSI code.
     *
     * @param code the ANSI escape code for the color or style
     */
    AnsiColor(String code) {
        this.code = code;
    }

    /**
     * Gets the ANSI code for this color or style.
     *
     * @return the ANSI code as a string
     */
    public String getCode() {
        return code;
    }

    /**
     * Applies this color or style to the given text.
     *
     * @param text the text to which the color or style will be applied
     * @return the text with ANSI escape codes applied
     */
    public String apply(String text) {
        return "\033[" + code + "m" + text + "\033[0m";
    }

    /**
     * Applies the given color to the provided text.
     *
     * @param color the color to apply
     * @param text  the text to which the color will be applied
     * @return the text with the color applied
     */
    public static String apply(AnsiColor color, String text) {
        return color.apply(text);
    }
    
    /**
     * Applies the given color and style to the provided text.
     *
     * @param color the color to apply
     * @param style the style to apply
     * @param text  the text to which the color and style will be applied
     * @return the text with both color and style applied
     */
    public static String apply(AnsiColor color, AnsiColor style, String text) {
        return "\033[" + color.getCode() + ";" + style.getCode() + "m" + text + "\033[0m";
    }

    /**
     * Applies an extended foreground color (256-color mode) to the given text.
     *
     * @param colorCode the 256-color code
     * @param text      the text to which the color will be applied
     * @return the text with the extended foreground color applied
     */
    public static String apply256Foreground(int colorCode, String text) {
        return "\033[38;5;" + colorCode + "m" + text + "\033[0m";
    }

    /**
     * Applies an extended background color (256-color mode) to the given text.
     *
     * @param colorCode the 256-color code
     * @param text      the text to which the color will be applied
     * @return the text with the extended background color applied
     */
    public static String apply256Background(int colorCode, String text) {
        return "\033[48;5;" + colorCode + "m" + text + "\033[0m";
    }

    /**
     * Applies a true color (24-bit RGB) foreground color to the given text.
     *
     * @param r   the red component (0-255)
     * @param g   the green component (0-255)
     * @param b   the blue component (0-255)
     * @param text the text to which the color will be applied
     * @return the text with the true color foreground applied
     */
    public static String applyTrueColorForeground(int r, int g, int b, String text) {
        return "\033[38;2;" + r + ";" + g + ";" + b + "m" + text + "\033[0m";
    }

    /**
     * Applies a true color (24-bit RGB) background color to the given text.
     *
     * @param r   the red component (0-255)
     * @param g   the green component (0-255)
     * @param b   the blue component (0-255)
     * @param text the text to which the color will be applied
     * @return the text with the true color background applied
     */
    public static String applyTrueColorBackground(int r, int g, int b, String text) {
        return "\033[48;2;" + r + ";" + g + ";" + b + "m" + text + "\033[0m";
    }

    /**
     * Formats text with multiple ANSI colors and styles.
     *
     * @param text   the text to format
     * @param colors the colors and styles to apply
     * @return the formatted text with ANSI escape codes applied
     */
    public static String format(String text, AnsiColor... colors) {
        StringBuilder sb = new StringBuilder();
        for (AnsiColor color : colors) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(color.getCode());
        }
        return "\033[" + sb.toString() + "m" + text + "\033[0m";
    }

    /**
     * Resets the foreground color to the default.
     *
     * @param text the text to which the reset will be applied
     * @return the text with the foreground color reset
     */
    public static String resetForeground(String text) {
        return RESET_FOREGROUND.apply(text);
    }

    /**
     * Resets the background color to the default.
     *
     * @param text the text to which the reset will be applied
     * @return the text with the background color reset
     */
    public static String resetBackground(String text) {
        return RESET_BACKGROUND.apply(text);
    }

    /**
     * Resets all colors and styles to the default.
     *
     * @param text the text to which the reset will be applied
     * @return the text with all colors and styles reset
     */
    public static String resetColors(String text) {
        return RESET.apply(text);
    }
}
