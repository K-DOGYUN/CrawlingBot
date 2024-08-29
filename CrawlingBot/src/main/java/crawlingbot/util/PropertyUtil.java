package crawlingbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
	private static Properties properties = new Properties();
	
	public static String getProps(String propId) {
		String rtnStr = "";
        // Load properties from file
        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream("token.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find properties");
                return null;
            }

            // Load properties from the input stream
            properties.load(input);

            // Get properties values
            rtnStr = properties.getProperty(propId);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return rtnStr;
	}
}
