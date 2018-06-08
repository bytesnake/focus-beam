package com.blubblub.FocusBeam;

import android.graphics.Point;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GameState {
    // we have eight channel, each five spectral bands
    final static int SIZE = 1;
    final static double UPDATE_RATE = 2; // maximum per 100ms

    private HashMap<String, ArrayList<DataSet>> player;
    private HashMap<String, Double> score;

    static public class DataSet {
        private double[] data;

        public DataSet(JSONArray arr) throws JSONException, IllegalArgumentException {
            data = new double[SIZE];

            if(arr.length() != SIZE) {
                Log.e("Focus Beam", "Data packet has a wrong size!");
                throw new IllegalArgumentException(arr.length() + " != " + SIZE);
            }

            for(int i = 0; i < arr.length(); i++)
                data[i] = arr.getDouble(i);
        }

        public double calculate() {
            return data[data.length - 1];
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

        return player.get(name).get(player.get(name).size()-1).calculate();

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

            //Log.d("Focus Beam", "Offset " + radius * ((coeff(name)/val)*Math.cos(Math.toRadians(360 * i / count))));

            i += 1;
        }

        //Log.d("Focus Beam", "Point at " + point.x + " : " + point.y);

        return point;
    }

    public void updateScore() {
        if(player.size() == 0)
            return;

        double val = 0.0;
        for (String name : names()) {
            val += coeff(name);
        }

        //val = Math.sqrt(val);

        if(val < 10e-5)
            return;

        for (String name : names()) {
            double update_score = 2 * (coeff(name) / val) - 1;

            //Log.d("Focus Beam", "Update: " + update_score);

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
