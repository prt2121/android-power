package com.luminiasoft.labs.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Speedometer extends View implements SpeedChangeListener {

    private static final String TAG = Speedometer.class.getSimpleName();

    private static final float DEFAULT_MAX_SPEED = 100;

    private static final int DEFAULT_SIZE = 300;

    // Speedometer internal state
    private float mMaxSpeed;

    private float mCurrentSpeed;

    // Scale drawing tools
    // paint of the colored area from -180 up to the current speed
    private Paint mOnPaint;

    // the grey or default paint
    private Paint mOffPaint;

    // paint for number on the scale
    private Paint mScalePaint;

    // paint for the reading digit
    private Paint mReadingPaint;

    private Path mOnPath;

    private Path mOffPath;

    private final RectF mOval = new RectF();

    // Drawing colors
    private int mOnColor = Color.argb(255, 0xff, 0xA5, 0x00);

    private int mOffColor = Color.argb(255, 0x3e, 0x3e, 0x3e);

    private int mScaleColor = Color.argb(255, 255, 255, 255);

    private float mScaleSize = 14f;

    private float mReadingSize = 60f;

    // Scale configuration
    private float mCenterX;

    private float mCenterY;

    private float mRadius;

    public Speedometer(Context context) {
        super(context);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Speedometer,
                0, 0);
        try {
            mMaxSpeed = a.getFloat(R.styleable.Speedometer_maxSpeed, DEFAULT_MAX_SPEED);
            mCurrentSpeed = a.getFloat(R.styleable.Speedometer_currentSpeed, 0);
            mOnColor = a.getColor(R.styleable.Speedometer_onColor, mOnColor);
            mOffColor = a.getColor(R.styleable.Speedometer_offColor, mOffColor);
            mScaleColor = a.getColor(R.styleable.Speedometer_scaleColor, mScaleColor);
            mScaleSize = a.getDimension(R.styleable.Speedometer_scaleTextSize, mScaleSize);
            mReadingSize = a.getDimension(R.styleable.Speedometer_readingTextSize, mReadingSize);
        } finally {
            a.recycle();
        }
        initDrawingTools();
    }

    private void initDrawingTools() {
        mOnPaint = new Paint();
        mOnPaint.setStyle(Paint.Style.STROKE);
        mOnPaint.setColor(mOnColor);
        mOnPaint.setStrokeWidth(35f);
        mOnPaint.setShadowLayer(5f, 0f, 0f, mOnColor);
        mOnPaint.setAntiAlias(true);

        mOffPaint = new Paint(mOnPaint);
        mOffPaint.setColor(mOffColor);
        mOffPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOffPaint.setShadowLayer(0f, 0f, 0f, mOffColor);

        mScalePaint = new Paint(mOffPaint);
        mScalePaint.setStrokeWidth(2f);
        mScalePaint.setTextSize(mScaleSize);
        mScalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        mScalePaint.setColor(mScaleColor);

        mReadingPaint = new Paint(mScalePaint);
        mReadingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOffPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        mReadingPaint.setTextSize(65f);
        mReadingPaint.setTypeface(Typeface.SANS_SERIF);
        mReadingPaint.setColor(Color.WHITE);

        mOnPath = new Path();
        mOffPath = new Path();
    }

    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setCurrentSpeed(float mCurrentSpeed) {
        if (mCurrentSpeed > this.mMaxSpeed) {
            this.mCurrentSpeed = mMaxSpeed;
        } else if (mCurrentSpeed < 0) {
            this.mCurrentSpeed = 0;
        } else {
            this.mCurrentSpeed = mCurrentSpeed;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + width + "x" + height);

        // Setting up the mOval area in which the arc will be drawn
        if (width > height) {
            mRadius = height / 4;
        } else {
            mRadius = width / 4;
        }
        mOval.set(mCenterX - mRadius,
                mCenterY - mRadius,
                mCenterX + mRadius,
                mCenterY + mRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//        Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        mCenterX = chosenDimension / 2;
        mCenterY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // mode == MeasureSpec.UNSPECIFIED
            return DEFAULT_SIZE;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawScaleBackground(canvas);
        drawOn(canvas);
        drawScaleNumber(canvas);
        drawReading(canvas);
    }

    /**
     * Draws the segments in their OFF state
     */
    private void drawScaleBackground(Canvas canvas) {
        canvas.drawARGB(255, 0, 0, 0);
        mOffPath.reset();
        for (int i = -180; i < 0; i += 4) {
            mOffPath.addArc(mOval, i, 4f);
        }
        canvas.drawPath(mOffPath, mOffPaint);
    }

    private void drawOn(Canvas canvas) {
        mOnPath.reset();
        for (int i = -180; i < (mCurrentSpeed / mMaxSpeed) * 180 - 180; i += 4) {
            mOnPath.addArc(mOval, i, 4f);
        }
        canvas.drawPath(mOnPath, mOnPaint);
    }

    private void drawScaleNumber(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(-180, mCenterX, mCenterY);
        Path circle = new Path();
        double halfCircumference = mRadius * Math.PI;
        double increments = 20;
        for (int i = 0; i < this.mMaxSpeed; i += increments) {
            circle.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);
            canvas.drawTextOnPath(String.format("%d", i),
                    circle,
                    (float) (i * halfCircumference / this.mMaxSpeed),
                    -30f,
                    mScalePaint);
        }

        canvas.restore();
    }

    private void drawReading(Canvas canvas) {
        Path path = new Path();
        String message = String.format("%d", (int) this.mCurrentSpeed);
        float[] widths = new float[message.length()];
        mReadingPaint.getTextWidths(message, widths);
        float advance = 0;
        for (double width : widths) {
            advance += width;
        }
        path.moveTo(mCenterX - advance / 2, mCenterY);
        path.lineTo(mCenterX + advance / 2, mCenterY);
        canvas.drawTextOnPath(message, path, 0f, 0f, mReadingPaint);
    }

    @Override
    public void onSpeedChanged(float newSpeedValue) {
        this.setCurrentSpeed(newSpeedValue);
        this.invalidate();
    }
}
