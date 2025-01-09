package Upgrades;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

public class UpgradeList {
    Map<String, Integer> upgrades = new HashMap<>();

    String filename = "src/main/resources/upgrades.txt";

    public UpgradeList() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                int tier = Integer.parseInt(parts[1]);
                upgrades.put(parts[0], tier);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read upgrades file.");
        }
    }
    /**
     * Gets the weighted upgrades based on the tier weights.
     *
     * @param tierWeights the weights for each tier
     * @return the weighted upgrades according to a given set of tier weights
     * @throws IllegalArgumentException if there are too few tier weights
     */
    public Map<String, Double> getWeigted(double[] tierWeights) {
        int maxTier = 0;
        for (int tier: upgrades.values()) {
            if (tier > maxTier) {
                maxTier = tier;
            }
        }
        if (maxTier > tierWeights.length - 1) {
            throw new IllegalArgumentException("Invalid tier weights.");
        }
        Map<String, Double> weightedUpgrades = new HashMap<>();
        for (Map.Entry<String, Integer> entry: upgrades.entrySet()) {
            weightedUpgrades.put(entry.getKey(), tierWeights[entry.getValue()]);
        }

        return weightedUpgrades;
    }
}
