package com.sommerpanage.helloax.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * TODO: document your custom view class.
 */
public class DotView extends View {

    private static final int DEFAULT_DOT_COLOR = R.color.defaultDotColor;

    private float mDotRadius = R.dimen.default_dot_radius;

    private List<DotDrawing> mDotsArray;
    private static final List<Paint> sPaintArray = new ArrayList<Paint>();

    private final Random mRandom = new Random();

    private float mOriginX;
    private float mOriginY;
    private float mWidth;
    private float mHeight;

    public DotView(Context context) {
        super(context);
        init(null, 0);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DotView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public ArrayList<? extends Parcelable> getDotsState() {
        return new ArrayList<DotDrawing>(mDotsArray);
    }

    public void setDotsState(List<? extends Parcelable> dots) {
        if (!dots.isEmpty() && dots.get(0) instanceof DotDrawing) {
            mDotsArray = (List<DotDrawing>) dots;
            invalidate();
        }

    }

    public void addRandomDot() {
        mDotsArray.add(new DotDrawing(getRandomDotCenterPoint(true),
                       getRandomDotCenterPoint(false),
                       mDotRadius,
                       getRandomPaintIndex()));
        invalidate();
    }

    public void removeLastDot() {
        if (!mDotsArray.isEmpty()) {
            mDotsArray.remove(mDotsArray.size() - 1);
            invalidate();
        }

    }

    public void clearDots() {
        mDotsArray.clear();
        invalidate();
    }

    private float getRandomDotCenterPoint(boolean isX) {
        return randomIntInRange((int) getMinDotCenterPoint(true), (int) getMaxDotCenterPoint(false));
    }

    private float getMinDotCenterPoint(boolean isX) {
        return (isX) ? (mOriginX + mDotRadius) : (mOriginY + mDotRadius);
    }

    private float getMaxDotCenterPoint(boolean isX) {
        return (isX) ? (mOriginX + mWidth - mDotRadius) : (mOriginY + mHeight - mDotRadius);
    }

    private int getRandomPaintIndex() {
        return randomIntInRange(0, sPaintArray.size() - 1);
    }

    private int randomIntInRange(int min, int max) {
        return mRandom.nextInt((max - min) + 1) + min;
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DotView, defStyle, 0);

        if (a != null) {

            mDotRadius = a.getDimension(R.styleable.DotView_dotRadius, mDotRadius);

            int color1 = a.getColor(R.styleable.DotView_dotColor1, DEFAULT_DOT_COLOR);
            int color2 = a.getColor(R.styleable.DotView_dotColor2, DEFAULT_DOT_COLOR);
            int color3 = a.getColor(R.styleable.DotView_dotColor3, DEFAULT_DOT_COLOR);

            if (sPaintArray.isEmpty()) {
                sPaintArray.add(getPaintForColor(color1));
                sPaintArray.add(getPaintForColor(color2));
                sPaintArray.add(getPaintForColor(color3));
            }

        }

        mDotsArray = new ArrayList<DotDrawing>();
    }

    private Paint getPaintForColor(int color) {
        Paint paint = new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mOriginX = getPaddingLeft();
        mOriginY = getPaddingTop();
        mWidth = w - (getPaddingLeft() + getPaddingRight());
        mHeight = h - (getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (DotDrawing dot : mDotsArray) {
            canvas.drawCircle(dot.getCenterX(), dot.getCenterY(), dot.getRadius(), sPaintArray.get(dot.getPaintIndex()));
        }

    }

    private class DotDrawing implements Parcelable{
        private float mCenterX;
        private float mCenterY;
        private float mRadius;
        private int mPaintIndex;

        public DotDrawing(float centerX, float centerY, float radius, int paintIndex) {
            mCenterX = centerX;
            mCenterY = centerY;
            mRadius = radius;
            mPaintIndex = paintIndex;
        }

        private DotDrawing(Parcel in) {
            mCenterX = in.readFloat();
            mCenterY = in.readFloat();
            mRadius = in.readFloat();
            mPaintIndex = in.readInt();
        }

        public float getCenterX() {
            return mCenterX;
        }

        public float getCenterY() {
            return mCenterY;
        }

        public void setCenterX(float centerX) {
            mCenterX = centerX;
        }

        public void setCenterY(float centerY) {
            mCenterY = centerY;
        }

        public float getRadius() {
            return mRadius;
        }

        public int getPaintIndex() {
            return mPaintIndex;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(mCenterX);
            dest.writeFloat(mCenterY);
            dest.writeFloat(mRadius);
            dest.writeInt(mPaintIndex);
        }

        public final Parcelable.Creator<DotDrawing> CREATOR
                = new Parcelable.Creator<DotDrawing>() {
            public DotDrawing createFromParcel(Parcel in) {
                return new DotDrawing(in);
            }

            public DotDrawing[] newArray(int size) {
                return new DotDrawing[size];
            }
        };
    }
}
