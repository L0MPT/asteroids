package Asteroids;

import Upgrades.Upgrade;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class UpgradeScreen {

    ArrayList<Upgrade> upgrades = new ArrayList<Upgrade>();

    SelectUpgrade selectUpgrade = new SelectUpgrade();

    Color color = new Color(200, 200, 255);

    /*
    *
    * @param player the player to upgrade
    */
    AsteroidPlayer player;

    public void display(Graphics2D g) {
        // Display the upgrade screen
        // g.drawRect(0, 0, 50, 50);
        g.setColor(color);

        // Display the upgrades
        for (int i = 0; i < upgrades.size(); i++) {
            // System.out.println(upgrades.get(i).getName());
            g.drawString(upgrades.get(i).getName(), 30, 40 + 40 * i);
        }
    }
    public void update() {
        
    }
    void initialize() {
        // TODO: make the amount of upgrades variable
        for (int i = 0; i < 5; i++) {
            upgrades.add(selectUpgrade.pick());
        }
    }
}
