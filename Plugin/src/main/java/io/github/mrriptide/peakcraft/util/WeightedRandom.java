package io.github.mrriptide.peakcraft.util;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedRandom<T extends Object> {
    ArrayList<T> items = new ArrayList<>();
    ArrayList<Integer> weights = new ArrayList<>();

    public void add(T item, int weight){
        items.add(item);
        weights.add(weight);
    }

    public T getRandom(){
        if (weights.size() == 0){
            return null;
        }
        int weightedChoice = ThreadLocalRandom.current().nextInt(0, weights.stream().mapToInt(Integer::intValue).sum());
        while (weightedChoice > 0){
            weightedChoice -= weights.get(0);
            weights.remove(0);
            items.remove(0);
        }

        return items.get(0);
    }
}
