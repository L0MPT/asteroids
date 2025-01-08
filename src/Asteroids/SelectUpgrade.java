package Asteroids;

import Upgrades.Upgrade;
import Upgrades.UpgradeList;

import Upgrades.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectUpgrade {
    UpgradeList upgradeList = new UpgradeList();
    // TODO: Change the weights
    Map<String, Double> weightedUpgrades = upgradeList.getWeigted(new double[] { 0.1, 0.3, 0.6 });

    public Upgrade pick() {
        double[] weights = weightedUpgrades.values().stream().mapToDouble(Double::doubleValue).toArray();
        double weightTotal = totalWeight(weights);
        double random = Math.random() * weightTotal;
        double sum = 0;
        String upgradeString = null;
        for (Map.Entry<String, Double> entry : weightedUpgrades.entrySet()) {
            sum += entry.getValue();
            if (sum > random) {
                upgradeString = entry.getKey();
                break;
            }
        }
        if (upgradeString == null) {
            throw new IllegalStateException("No upgrade selected.");
        }

        // turns the string into an Upgrade instance
        Upgrade upgrade = getUpgrade(upgradeString);
        return upgrade;
    }

    private Upgrade getUpgrade(String upgradeString) {
        try {
            String upgradeClass = "Upgrades." + upgradeString;
            Class<?> upgrade = Class.forName(upgradeClass);
            // BROKEN:
            return (Upgrade) upgrade.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid upgrade: " + upgradeString);
        }
    }

    private double totalWeight(double[] weights) {
        double total = 0;
        for (double weight : weights) {
            total += weight;
        }
        return total;
    }

}
