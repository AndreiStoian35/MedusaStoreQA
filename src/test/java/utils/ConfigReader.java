package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

public class ConfigReader {

    static Properties prop = new Properties();

    static{
        try(InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")){

            prop.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Could not parse config.properties file" + e);
        }
    }


    public static boolean getHeadless(){
        String headless = getValue("headless");

        if (Objects.equals(headless, "true") || Objects.equals(headless, "false"))
            return Boolean.parseBoolean(headless);

        else throw new RuntimeException("Headless property value is unsupported");
    }

    public static String apiBaseUrl(){
        return getValue("storeApiUrl");
    }

    public static String getApiKey(){
        return getValue("apiKey");
    }

    private static String getValue(String key){

        String systemValue = System.getProperty(key);
        String propValue;

        if(systemValue == null || systemValue.isBlank()){
            propValue = prop.getProperty(key);
            if (propValue == null || propValue.isBlank())
                throw new RuntimeException("Value not found in config.properties for key" + key);

            return propValue;

        }
        return systemValue;
    }
}
