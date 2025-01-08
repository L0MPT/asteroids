package Upgrades;
import Asteroids.AsteroidPlayer;

public class BigBoy implements Upgrade {
    @Override
    public String getName() {
        return "Big Boy";
    }
    @Override
    public String getDescription() {
        return "Makes you much bigger and healthier.";
    }
    @Override
    public void onPick(AsteroidPlayer player) {
        player.setWidth(player.getWidth() * 2);
        player.setHeight(player.getHeight() * 2);
        player.setHealthMax(player.getHealthMax() * 2);
    }
    
}
