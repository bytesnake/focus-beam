package com.blubblub.app_versuch2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;

public class keinPlan extends AppCompatActivity {
    private GameState state;
    private GameFieldView game_field;
    private Subscriber subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kein_plan);

        state = new GameState();
        game_field = (GameFieldView)findViewById(R.id.game_field);
        subscribe = new Subscriber(this);

        subscribe.setListener(new SubscriberListener() {
            @Override
            public void gotValues(String topic, ArrayList<Double> values) {
                // update the state with new values of player "topic"
                state.update(topic, values);
                // update the game field according to the state
                game_field.update(state);
            }
        });

    }

    static public void log(Activity act, String text) {
        Log.d("Focus Beam", text);
        Snackbar.make(act.findViewById(R.id.game_field), text, Snackbar.LENGTH_LONG);
    }
}
