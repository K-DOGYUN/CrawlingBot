package crawlingbot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class PropertyUtil {
	private static Properties properties = new Properties();
	
	public static String getProps(String propId) {
		String rtnStr = "";
		String tokens = "";
		
        Yaml yaml = new Yaml();
        yaml.load(tokens);
        try (InputStream in = new FileInputStream("src/main/resources/config.yaml")) {
            Map<String, Object> config = yaml.load(in);
            Map<String, String> profile = (Map<String, String>) config.get(System.getProperty("environment"));
            tokens = profile.get("tokens").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Load properties from file
        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream(tokens)) {
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
