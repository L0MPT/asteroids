package Upgrades;

import Asteroids.*;

/**
 * The Upgrade interface represents an upgrade in the game that can affect the
 * player's abilities.
 * Implementations of this interface should provide specific behaviors for each
 * method.
 */
public interface Upgrade {

    /**
     * Gets the name of the upgrade.
     *
     * @return the name of the upgrade
     */
    String getName();

    /**
     * Gets the description of the upgrade.
     *
     * @return the description of the upgrade
     */
    String getDescription();

    /**
     * Defines the behavior of the upgrade when the player picks it up.
     * Defaults to doing nothing.
     *
     * @param player the player that picks up the upgrade
     */
    default void onPick(AsteroidPlayer player) {
    }

    /**
     * Defines the behavior of the upgrade when the player shoots.
     * Defaults to doing nothing.
     * 
     * @param bullet the bullet that the player shoots
     */
    default void onShoot(Bullet bullet) {
    }

    /**
     * Defines the behavior of the upgrade when the player gets hurt.
     * Defaults to doing nothing.
     */
    default void onHurt() {
    }

    /**
     * Defines the behavior of the upgrade when the player uses thrust.
     * Defaults to doing nothing.
     */
    default void onThrust(AsteroidPlayer player) {
    }
}