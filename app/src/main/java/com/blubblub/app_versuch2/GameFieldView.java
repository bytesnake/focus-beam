package com.blubblub.app_versuch2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

public class GameFieldView extends View {
    final static double CIRCLE_RADIUS = 300;
    final static double PLAYER_RADIUS = 50;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private int height;
    private int width;
    private String selected;
    private GameState state;

    public GameFieldView(Context c, AttributeSet attrs) {
        super(c, attrs);

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        selected = "";
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        height = h;
        width = w;
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float mid_y = height / 2.0f;
        float mid_x = width / 2.0f;

        int num = state.getPlayer().size();
        int i = 0;
        for (String name : state.getPlayer().keySet()) {
            double pl_x = mid_x + CIRCLE_RADIUS * Math.cos((double) i / num * 2 * Math.PI);
            double pl_y = mid_y + CIRCLE_RADIUS * Math.sin((double) i / num * 2 * Math.PI);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(6f);
            mPaint.setColor(Color.RED);
            canvas.drawCircle((float)pl_x, (float)pl_y, (float)PLAYER_RADIUS, mPaint);
            canvas.drawLine((float)pl_x )

            i += 1;
        }

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float)(0.15+alpha*(0.85-0.15))*width, y, radius/2.0f, mPaint);

        canvas.drawLine(0.15f*width + radius, y, 0.85f*width-radius, y, mPaint);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        final float x = event.getX();
        final float y = event.getY();

        int num = state.getPlayer().size();
        int i = 0;
        for (String name : state.getPlayer().keySet()) {
            double pl_x = CIRCLE_RADIUS * Math.cos((double)i / num * 2 * Math.PI);
            double pl_y = CIRCLE_RADIUS * Math.sin((double)i / num * 2 * Math.PI);

            if(Math.sqrt(Math.pow(pl_x-x,2) + Math.pow(pl_y-y,2)) < PLAYER_RADIUS) {
                selected = name;
            }

            i += 1;
        }
    }

    public void update(GameState sta) {
        state = sta;

        invalidate();
    }
}
