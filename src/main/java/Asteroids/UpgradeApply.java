package Asteroids;
import Upgrades.Upgrade;
import java.util.ArrayList;

/**
 * The UpgradeApply class manages a list of upgrades and applies their effects
 * during specific game events such as shooting, getting hurt, and thrusting.
 */
public class UpgradeApply {
    ArrayList<Upgrade> upgrades = new ArrayList<Upgrade>();

    public void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
    }

    public void applyShoot(Bullet[] bullets) {
        for(Bullet bullet : bullets) {
            for (Upgrade upgrade : upgrades) {
                upgrade.onShoot(bullet);
            }
        }
    }
    public void applyShoot(Bullet bullet) {
        for (Upgrade upgrade : upgrades) {
            upgrade.onShoot(bullet);
        }
    }

    public void applyHurt() {
        for (Upgrade upgrade : upgrades) {
            upgrade.onHurt();
        }
    }

    public void applyThrust(AsteroidPlayer player) {
        for (Upgrade upgrade : upgrades) {
            upgrade.onThrust(player);
        }
    }


}