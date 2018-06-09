package com.blubblub.FocusBeam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
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

        // set desired painting properties
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);


        // at the beginning no player is selected
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

        //get colours
/*        int color_background = ContextCompat.getColor(context, R.color.color_background);
        int color_beam = ContextCompat.getColor(context, R.color.color_beam);
        int color_players = ContextCompat.getColor(context, R.color.color_players);
        int color_text = ContextCompat.getColor(context, R.color.color_text);
        int color_ball = ContextCompat.getColor(context, R.color.color_ball);
*/

        // get point
        Point point = state.getPoint((int)(CIRCLE_RADIUS - 2*PLAYER_RADIUS));
        point.x = point.x + (int)mid_x;
        point.y = point.y + (int)mid_y;

        int num = state.size();
        int i = 0;
        for (String name : state.names()) {
            double pl_x = mid_x + CIRCLE_RADIUS * Math.cos(Math.toRadians(360 * i / num ));
            double pl_y = mid_y + CIRCLE_RADIUS * Math.sin(Math.toRadians(360 * i / num ));

            mPaint.setStrokeWidth(3f);
            if(name == selected)
                mPaint.setColor(R.color.color_players);
            else
                mPaint.setColor(R.color.color_beam);
            canvas.drawLine((float)pl_x, (float)pl_y, point.x, point.y, mPaint);

            mPaint.setStrokeWidth(6f);
            if(name == selected) {
                mPaint.setStyle(Paint.Style.STROKE);
                //mPaint.setColor(R.color.color_beam);
                canvas.drawCircle((float)pl_x, (float)pl_y, (float)PLAYER_RADIUS, mPaint);
            }

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(R.color.color_players);
            canvas.drawCircle((float)pl_x, (float)pl_y, (float)PLAYER_RADIUS, mPaint);

            mPaint.setColor(R.color.color_text);
            mPaint.setTextSize(30);
            String text = Integer.toString((int)state.getScore(name));
            float textWidth = mPaint.measureText(text);
            int xOffset = (int)((textWidth)/2f);
            canvas.drawText(text, (float)pl_x - xOffset, (float)pl_y + 10, mPaint);

            i += 1;
        }

        mPaint.setColor(R.color.color_ball);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(point.x, point.y, (float)PLAYER_RADIUS / 2.0f, mPaint);

        mPaint.setColor(R.color.color_text);
        if(!selected.equals("")) {
            mPaint.setTextSize(25f);
           canvas.drawText("Player - " + selected, 20, 160, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() != 0)
            return false;

        final float x = event.getX();
        final float y = event.getY();

        int num = state.size();
        int i = 0;
        for (String name : state.names()) {
            // calculate the player circle center
            double pl_x = width / 2.0f + CIRCLE_RADIUS * Math.cos(Math.toRadians(360 * i / num ));
            double pl_y = height / 2.0f + CIRCLE_RADIUS * Math.sin(Math.toRadians(360 * i / num ));

            // check if the touch event lies within the player circle
            if(Math.sqrt(Math.pow(pl_x-x,2) + Math.pow(pl_y-y,2)) < PLAYER_RADIUS) {
                selected = name;

                return false;
            }

            i += 1;
        }

        // if no player circle was touched, then we assume that the user meant to
        // select no player
        selected = "";

        return false;
    }

    public void update() {
        invalidate();
    }
}
