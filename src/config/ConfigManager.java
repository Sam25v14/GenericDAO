package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
	public static final String PATH = System.getProperty("user.dir") + "/config/";
	
	public static Properties loadConfig(String file) {
		Properties prop = new Properties();
		
		FileInputStream ip;
		try {
			ip = new FileInputStream(PATH + file);
			prop.load(ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prop;
	}
}
