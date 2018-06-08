package com.blubblub.app_versuch2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GameFieldView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private int height;
    private int width;
    private double alpha;

    public GameFieldView(Context c, AttributeSet attrs) {
        super(c, attrs);

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        alpha = 0.0;
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

        int radius = 60;
        int y = height / 2;

        // draw the left circle
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6f);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(0.15f*width, y, radius, mPaint);
        canvas.drawCircle(0.85f*width, y, radius, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float)(0.15+alpha*(0.85-0.15))*width, y, radius/2.0f, mPaint);

        canvas.drawLine(0.15f*width + radius, y, 0.85f*width-radius, y, mPaint);
    }

    public void clearCanvas() {
        invalidate();
    }

    public void setAlpha(double lAlpha) {
        alpha = lAlpha;
    }
}
