package Asteroids;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

public class InputHandler {
    static Properties prop = new Properties();

    public InputHandler() {
        
        try (FileReader reader = new FileReader("src/main/resources/keybinds.properties")) {
            prop.load(reader);;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBind(String key) {
        return Integer.parseInt(prop.getProperty(key));
    }

    public static int getPlayerBind(String key, int player) {
        return Integer.parseInt(prop.getProperty(key + player));
    }

    public static boolean onceBind(String key) {
        if (Asteroids.keys[getBind(key)]) {
            Asteroids.keys[getBind(key)] = false;
            return true;
        }
        return false;
    }

    public static boolean oncePlayerBind(String key, int player) {

        if (Asteroids.keys[getPlayerBind(key, player)]) {
            Asteroids.keys[getPlayerBind(key, player)] = false;
            return true;
        }
        return false;
    }

    public static boolean heldBind(String key) {
        return Asteroids.keys[getBind(key)];
    }

    public static boolean heldPlayerBind(String key, int player) {
        return Asteroids.keys[getPlayerBind(key, player)];
    }
}
