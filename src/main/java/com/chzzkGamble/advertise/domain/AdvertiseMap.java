package com.chzzkGamble.advertise.domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class AdvertiseMap {

    private static final Advertise DEFAULT_ADVERTISE = new Advertise("IMAGE_URL_HERE", 0, true);

    private final Map<Advertise, Long> adCumulativeCosts;
    private final Map<Advertise, Double> adProbabilities;
    private final long totalAmount;

    private AdvertiseMap(Map<Advertise, Long> adCumulativeCosts, Map<Advertise, Double> adProbabilities, long totalAmount) {
        this.adCumulativeCosts = adCumulativeCosts;
        this.adProbabilities = adProbabilities;
        this.totalAmount = totalAmount;
    }

    public static AdvertiseMap from(List<Advertise> advertises) {
        long totalAmount = 0;
        Map<Advertise, Long> costMap = new LinkedHashMap<>();
        Map<Advertise, Double> probabilityMap = new HashMap<>();

        for (Advertise advertise : advertises) {
            if (!advertise.isApproved()) {
                continue;
            }
            totalAmount += advertise.getCost();
            costMap.put(advertise, totalAmount);
        }
        for (Advertise advertise : advertises) {
            if (!advertise.isApproved()) {
                continue;
            }
            probabilityMap.put(advertise, advertise.getCost() / (double) totalAmount);
        }

        return new AdvertiseMap(costMap, probabilityMap, totalAmount);
    }

    public Advertise getRandom() {
        if (totalAmount == 0) return DEFAULT_ADVERTISE;

        long threshold = ThreadLocalRandom.current().nextLong(totalAmount);
        return adCumulativeCosts.entrySet().stream()
                .filter(entry -> threshold <= entry.getValue())
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(DEFAULT_ADVERTISE);
    }

    public Map<Advertise, Double> getProbabilities() {
        return Map.copyOf(adProbabilities);
    }
}
