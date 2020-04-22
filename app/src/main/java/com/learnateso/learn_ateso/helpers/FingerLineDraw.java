package com.learnateso.learn_ateso.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Emo on 4/7/2017.
 */

public class FingerLineDraw extends View {
    private final Paint mPaint;
    private Bitmap mBitmap;
    private Canvas  mCanvas;
    private Rect mRectangle;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    // Define the text to draw on canvas
    String txt = "ANDROID";

    public FingerLineDraw(Context context) {
        this(context, null);
    }

    public FingerLineDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        //mPaint.setStrokeWidth(10);
        // Set the text size
        mPaint.setTextSize(25);
        // Text alignment
        mPaint.setTextAlign(Paint.Align.CENTER);
        // Initialize a typeface object to draw text on canvas
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

        // Set the paint font
        mPaint.setTypeface(typeface);

        // calculate the y position of canvas to draw the text
        //float w = mPaint.measureText(txt,0,txt.length());
        mRectangle = new Rect();

        mPaint.getTextBounds(
                txt, // text
                0, // start
                txt.length(), // end
                mRectangle); // bounds
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = mPaint.measureText(txt,0,txt.length());
        canvas.drawRect(mRectangle.height(),mRectangle.height() + Math.abs(mRectangle.height()),w+25,150,mPaint);
        canvas.drawLine(startX, startY, endX, endY, mPaint);

        //Finally, Draw the text on the canvas at the horizontal and vertical center position
        canvas.drawText(
                txt, // Text to draw
                150, // x canvas.getWidth()/2
                100 + Math.abs(mRectangle.height())/2, // y canvas.getHeight()/2
                mPaint // Paint
        );
        System.out.println("canvas width = "+ canvas.getWidth());
        System.out.println("canvas height = "+ canvas.getHeight());
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                System.out.println("X coordinate = "+ event.getX());
                System.out.println("Y coordinate = "+ event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                invalidate();
                break;
        }
        return true;
    }
}