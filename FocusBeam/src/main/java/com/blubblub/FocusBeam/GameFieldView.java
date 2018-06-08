package com.blubblub.FocusBeam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameFieldView extends View {
    final static double CIRCLE_RADIUS = 300;
    final static double PLAYER_RADIUS = 50;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private int height;
    private int width;
    private String selected;
    public GameState state;

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

        // get point
        Point point = state.getPoint((int)(CIRCLE_RADIUS - PLAYER_RADIUS));
        point.x = point.x + (int)mid_x;
        point.y = point.y + (int)mid_y;

        int num = state.size();
        int i = 0;
        for (String name : state.names()) {
            double pl_x = mid_x + CIRCLE_RADIUS * Math.cos(Math.toRadians(360 * i / num ));
            double pl_y = mid_y + CIRCLE_RADIUS * Math.sin(Math.toRadians(360 * i / num ));

            mPaint.setStrokeWidth(3f);
            if(name == selected)
                mPaint.setColor(Color.BLUE);
            else
                mPaint.setColor(Color.BLACK);
            canvas.drawLine((float)pl_x, (float)pl_y, point.x, point.y, mPaint);

            mPaint.setStrokeWidth(6f);
            if(name == selected) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle((float)pl_x, (float)pl_y, (float)PLAYER_RADIUS, mPaint);
            }

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.RED);
            canvas.drawCircle((float)pl_x, (float)pl_y, (float)PLAYER_RADIUS, mPaint);

            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(30);
            String text = Integer.toString((int)state.getScore(name));
            float textWidth = mPaint.measureText(text);
            int xOffset = (int)((textWidth)/2f);
            canvas.drawText(text, (float)pl_x - xOffset, (float)pl_y + 10, mPaint);

            i += 1;
        }

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(point.x, point.y, (float)PLAYER_RADIUS / 2.0f, mPaint);

        if(selected != "") {
           canvas.drawText("Player: " + selected, 10, 40, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("Event", "Event: " + event.getAction());

        if(event.getAction() != 0) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        Log.d("MouseEvent", "Got clikc at " + x + " : " + y);

        int num = state.size();
        int i = 0;
        for (String name : state.names()) {
            double pl_x = width / 2.0f + CIRCLE_RADIUS * Math.cos(Math.toRadians(360 * i / num ));
            double pl_y = height / 2.0f + CIRCLE_RADIUS * Math.sin(Math.toRadians(360 * i / num ));
            Log.d("Focus Beam", "Player " + pl_x + " : " + pl_y);
            Log.d("Focus Beam", "Distance: " + Math.sqrt(Math.pow(pl_x-x,2) + Math.pow(pl_y-y,2)));
            if(Math.sqrt(Math.pow(pl_x-x,2) + Math.pow(pl_y-y,2)) < PLAYER_RADIUS) {
                Log.d("Focus Beam", "Selected: " + name);
                selected = name;
            }

            i += 1;
        }

        return false;
    }

    public void update() {
        invalidate();
    }
}
