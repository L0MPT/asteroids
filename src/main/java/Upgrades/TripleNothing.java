package Upgrades;
import Asteroids.AsteroidPlayer;
import Asteroids.Bullet;

public class TripleNothing implements Upgrade {
    @Override
    public String getName() {
        return "Triple Nothing";
    }
    @Override
    public String getDescription() {
        return "Reduces the player's bullet count by 2 and triples damage.";
    }
    @Override
    public void onPick(AsteroidPlayer player) {
        player.setAmmoMax(player.getAmmoMax() - 2);
    }
    @Override
    public void onShoot(Bullet bullet) {
        bullet.setDamage(bullet.getDamage() * 3);
    }
}