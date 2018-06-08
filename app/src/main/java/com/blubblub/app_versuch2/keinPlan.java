package com.blubblub.app_versuch2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class keinPlan extends AppCompatActivity {
    private GameFieldView game_field;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kein_plan);

        game_field = (GameFieldView)findViewById(R.id.game_field);
        server = new Server(this, game_field);
        server.start();
    }
}
