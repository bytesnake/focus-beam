package com.blubblub.FocusBeam;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GameState {
    // we have eight channel, each five spectral bands
    final static int SIZE = 32;
    final static double UPDATE_RATE = 2; // maximum per 100ms

    private HashMap<String, ArrayList<DataSet>> player;
    private HashMap<String, Double> score;

    static public class DataSet {
        private double[] data;

        public DataSet(JSONObject obj) throws JSONException, IllegalArgumentException {
            data = new double[SIZE];

            Iterator<String> keys = obj.keys();

            while(keys.hasNext()) {
                String s = keys.next();
                JSONArray arr = obj.getJSONObject(s).getJSONObject("block").getJSONArray("data").getJSONArray(0).getJSONArray(0);

                if(arr.length() != 8)
                    throw new IllegalArgumentException(arr.length() + " != " + 8);

                int i;
                switch(s) {
                    case "alpha": i = 0; break;
                    case "beta": i = 8; break;
                    case "delta": i = 16; break;
                    case "theta": i = 24; break;
                    default: throw new IllegalArgumentException("Wrong band name: " + s);
                }

                for(int j = i; j < i+8; j++)
                    data[j] = arr.getDouble(j - i );

            }
        }

        public double[] calculate() {
            double[] arr = new double[8];

            for(int i = 0; i < 8; i++) {
                if((data[0 + i] + data[24 + i]) < 10e-10)
                    arr[i] = 0;
                else
                    arr[i] = data[8 + i] / ((data[0 + i] + data[24 + i]));
            }

            //Log.d("Focus Beam", "Arr: " + Arrays.toString(data));

            return arr;
        }

        public double calculate_max() {
            double max_elm = 0.0;

            for(double elm : calculate()) {
                if(max_elm < elm)
                    max_elm = elm;
            }

            Log.d("Focus Beam", "Max value: " + max_elm*1000);

            return max_elm * 10000;
        }
    }

    public GameState() {
        player = new HashMap<>();
        score = new HashMap<>();
    }

    public void update(String name, DataSet data) {
        ArrayList<DataSet> val = (player.containsKey(name) ? player.get(name):new ArrayList());

        val.add(data);
        player.put(name, val);
    }

    public int size() {
        return player.size();
    }

    public Set<String> names() {
        return player.keySet();
    }

    public double coeff(String name) {
        if(player.get(name).size() == 0)
            return 0.0;

        double val = 0;
        int pos = player.get(name).size()-1;

        while(val == 0 && pos > 0) {
            val = player.get(name).get(pos).calculate_max();

            pos-=1;
        }

        return val;

    }

    public Point getPoint(int radius) {
        Point point = new Point(0,0);

        double val = 0.0;
        for (String name : names()) {
            val += coeff(name);
        }

        int i = 0;
        int count = names().size();

        for (String name : names()) {
            point.offset(
                    (int)(radius * (coeff(name)/val)*Math.cos(Math.toRadians(360 * i / count))),
                    (int)(radius * (coeff(name)/val)*Math.sin(Math.toRadians(360 * i / count)))
            );

            i += 1;
        }

        return point;
    }

    public void updateScore() {
        if(player.size() == 0)
            return;

        double val = 0.0;
        for (String name : names()) {
            val += coeff(name);
        }

        if(val < 10e-5)
            return;

        for (String name : names()) {
            double update_score = 2 * (coeff(name) / val) - 1;

            if(score.containsKey(name))
                score.put(name, score.get(name) + update_score * UPDATE_RATE);
            else
                score.put(name, update_score * UPDATE_RATE);
        }
    }

    public double getScore(String name) {
        if(!score.containsKey(name))
            score.put(name, 0.0);

        return score.get(name);
    }
}
