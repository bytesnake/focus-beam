package com.blubblub.app_versuch2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    private HashMap<String, ArrayList<Double>> player;

    public GameState() {
        player = new HashMap<>();
    }

    public void update(String name, ArrayList<Double> values) {
        ArrayList<Double> val;
        if(!player.containsKey(name))
            val = values;
        else {
            val = player.get(name);
            val = player.get(name);
            val.addAll(values);
        }

        player.put(name, val);
    }

    public HashMap<String, ArrayList<Double>> getPlayer() {
        return player;
    }
}
