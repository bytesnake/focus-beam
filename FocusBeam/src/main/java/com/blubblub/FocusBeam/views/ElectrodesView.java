package com.blubblub.FocusBeam.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blubblub.FocusBeam.GameState;

public class ElectrodesView extends View {
    private Paint mPaint;
    private GameState state;

    public ElectrodesView(Context c, AttributeSet attrs, GameState _state) {
        super(c, attrs);

        // set a name
        setTag("Electrodes View");

        // set desired painting properties
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        state = _state;
    }
}
