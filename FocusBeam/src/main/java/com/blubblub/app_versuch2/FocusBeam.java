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
import java.util.Timer;
import java.util.TimerTask;

public class FocusBeam extends AppCompatActivity {
    private GameState state;
    private GameFieldView game_field;
    private Subscriber subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kein_plan);

        // wire everything
        state = new GameState();
        game_field = (GameFieldView)findViewById(R.id.game_field);
        game_field.state = state;
        subscribe = new Subscriber(this);

        subscribe.setListener(new SubscriberListener() {
            @Override
            public void gotValues(String topic, GameState.DataSet val) {
                // update the state with new values of player "topic"
                state.update(topic, val);
                // update the game field according to the state
                game_field.update();
            }
        });

        // we have a continuously running score, update it every 100ms
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                state.updateScore();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        game_field.invalidate();
                    }
                });
            }
        },1000,100);

    }

    static public void log(Activity act, String text) {
        Log.d("Focus Beam", text);
        Snackbar.make(act.findViewById(R.id.game_field), text, Snackbar.LENGTH_LONG);
    }
}
