import twitter4j.TwitterException;

import java.io.*;
import java.util.Properties;

public class Main {

    private static final String PROPERTIES_FILE_PATH = "resources/OAuth.properties";
    private static final String[] PROPERTIES_LIST = {"OAuthConsumerKey",
                                                        "OAuthConsumerSecret",
                                                        "OAuthAccessToken",
                                                        "OAuthAccessSecret"};

    public static void main(String[] args) {
        FileInputStream propertiesFile = null;
        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE_PATH);
        } catch (FileNotFoundException e) {
            createFile(PROPERTIES_FILE_PATH);
            System.out.println("Relaunch the program and enter correct credentials in OAuth.properties");
            System.exit(0);
        }

        Properties properties = new Properties();
        try {
            properties.load(propertiesFile);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(0);
        }
        if (!checkCredentialsIsSet(properties)) {
            System.err.println("Credentials aren't set");
            System.exit(0);
        }
        TweetDestroyer tweetDestroyer = new TweetDestroyer(properties.getProperty("OAuthConsumerKey"),
                                                            properties.getProperty("OAuthConsumerSecret"),
                                                            properties.getProperty("OAuthAccessToken"),
                                                            properties.getProperty("OAuthAccessSecret"));
        tweetDestroyer.build();
        try {
            tweetDestroyer.deleteTweets(300);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Deleted Tweets:" + tweetDestroyer.getTweetsDeleted());
    }

    private static void createFile(String path) {
        File file = new File(path);
        Properties properties = new Properties();
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileInputStream fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                for (String property : PROPERTIES_LIST) {
                    properties.setProperty(property, "not_set");
                }
                properties.store(new FileOutputStream(file), "OAuth Properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkCredentialsIsSet(Properties properties) {
        for (String property : PROPERTIES_LIST) {
            if (properties.get(property).equals("not_set")) {
                return false;
            }
        }
        return true;
    }
}