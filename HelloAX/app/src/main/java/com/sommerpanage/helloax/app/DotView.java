package com.sommerpanage.helloax.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

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
    private static final List<String> sDotNameArray = new ArrayList<String>();

    private final Random mRandom = new Random();

    private float mOriginX;
    private float mOriginY;
    private float mWidth;
    private float mHeight;
    private float mPreviousWidth;
    private float mPreviousHeight;

    private DotViewTouchHelper mDotViewTouchHelper;

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

    public void addRandomDot() {
        mDotsArray.add(new DotDrawing(getRandomDotCenterPoint(true),
                getRandomDotCenterPoint(false),
                mDotRadius,
                getRandomPaintIndex()));
        invalidate();

        // Accessibility
        final int size = mDotsArray.size();
        final String dotName = sDotNameArray.get(mDotsArray.get(size - 1).getPaintIndex());
        if (size == 1) {
            announceForAccessibility(String.format(getResources().
                    getString(R.string.ax_action_added_dot_1), dotName));
        } else {
            announceForAccessibility(String.format(getResources().
                    getString(R.string.ax_action_added_dot), dotName, size));
        }
    }

    public void removeLastDot() {
        if (!mDotsArray.isEmpty()) {
            // Accessibility
            final int index = mDotsArray.size() - 1;
            final String dotName = sDotNameArray.get(mDotsArray.get(index).getPaintIndex());
            mDotsArray.remove(index);
            invalidate();

            // Accessibility
            final int size = mDotsArray.size();
            if (size == 1) {
                announceForAccessibility(String.format(getResources().
                        getString(R.string.ax_action_removed_dot_1), dotName));
            } else {
                announceForAccessibility(String.format(getResources().
                        getString(R.string.ax_action_removed_dot), dotName, size));
            }
        } else {
            announceForAccessibility(getResources().getString(R.string.ax_cannot_remove_dots));
        }

    }

    public void clearDots() {
        mDotsArray.clear();
        invalidate();

        announceForAccessibility(getResources().getString(R.string.ax_action_cleared_dots));
    }

    private float getRandomDotCenterPoint(boolean isX) {
        return randomIntInRange((int) getMinDotCenterPoint(isX), (int) getMaxDotCenterPoint(isX));
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

        mDotRadius = a.getDimension(R.styleable.DotView_dotRadius, mDotRadius);

        int color1 = a.getColor(R.styleable.DotView_dotColor1, DEFAULT_DOT_COLOR);
        int color2 = a.getColor(R.styleable.DotView_dotColor2, DEFAULT_DOT_COLOR);
        int color3 = a.getColor(R.styleable.DotView_dotColor3, DEFAULT_DOT_COLOR);

        if (sPaintArray.isEmpty()) {
            sPaintArray.add(getPaintForColor(color1));
            sPaintArray.add(getPaintForColor(color2));
            sPaintArray.add(getPaintForColor(color3));
        }

        if (sDotNameArray.isEmpty()) {
            sDotNameArray.add(a.getString(R.styleable.DotView_dotColor1Name));
            sDotNameArray.add(a.getString(R.styleable.DotView_dotColor2Name));
            sDotNameArray.add(a.getString(R.styleable.DotView_dotColor3Name));
        }

        mDotsArray = new ArrayList<DotDrawing>();

        mDotViewTouchHelper = new DotViewTouchHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mDotViewTouchHelper);
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

        final boolean sizeRefreshed = (mPreviousWidth != 0 && mWidth != mPreviousWidth) ||
                                      (mPreviousHeight != 0 && mHeight != mPreviousHeight);
        if (sizeRefreshed) {
            for (DotDrawing dot : mDotsArray) {
                dot.setCenterX((float) Math.floor((double) dot.getCenterX() * mWidth/mPreviousWidth));
                dot.setCenterY((float) Math.floor((double) dot.getCenterY() * mHeight/mPreviousHeight));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (DotDrawing dot : mDotsArray) {
            canvas.drawCircle(dot.getCenterX(), dot.getCenterY(), dot.getRadius(), sPaintArray.get(dot.getPaintIndex()));
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                final int dotIndex = getDotIndexUnder(event.getX(), event.getY());
                if (dotIndex >= 0) {
                    onDotClicked(dotIndex);
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private boolean onDotClicked(int index) {
        final DotDrawing dot = mDotsArray.get(index);
        if (dot == null) {
            return false;
        }

        dot.setPaintIndex((dot.getPaintIndex() + 1) % sPaintArray.size());
        invalidate();

        mDotViewTouchHelper.invalidateVirtualView(index);
        mDotViewTouchHelper.sendEventForVirtualView(index, AccessibilityEvent.TYPE_VIEW_CLICKED);

        return true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedDotViewState ss = new SavedDotViewState(superState);
        ss.setSavedDots(mDotsArray);
        ss.setSavedWidth(mWidth);
        ss.setSavedHeight(mHeight);

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedDotViewState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedDotViewState ss = (SavedDotViewState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mDotsArray = ss.getSavedDots();
        mPreviousWidth = ss.getSavedWidth();
        mPreviousHeight = ss.getSavedHeight();
    }

    private static class SavedDotViewState extends BaseSavedState {
        private List<DotDrawing> mSavedDots;
        private float mSavedWidth;
        private float mSavedHeight;

        public List<DotDrawing> getSavedDots() {
            return mSavedDots;
        }

        public void setSavedDots(List<DotDrawing> savedDots) {
            mSavedDots = savedDots;
        }

        public float getSavedWidth() {
            return mSavedWidth;
        }

        public void setSavedWidth(float savedWidth) {
            mSavedWidth = savedWidth;
        }

        public float getSavedHeight() {
            return mSavedHeight;
        }

        public void setSavedHeight(float savedHeight) {
            mSavedHeight = savedHeight;
        }


        SavedDotViewState(Parcelable superState) {
            super(superState);
        }

        private SavedDotViewState(Parcel in) {
            super(in);
            if (mSavedDots == null) {
                mSavedDots = new ArrayList<DotDrawing>();
            }
            in.readTypedList(mSavedDots, DotDrawing.CREATOR);
            mSavedWidth = in.readFloat();
            mSavedHeight = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeTypedList(mSavedDots);
            out.writeFloat(mSavedWidth);
            out.writeFloat(mSavedHeight);
        }

        public static final Parcelable.Creator<SavedDotViewState> CREATOR =
                new Parcelable.Creator<SavedDotViewState>() {
                    public SavedDotViewState createFromParcel(Parcel in) {
                        return new SavedDotViewState(in);
                    }
                    public SavedDotViewState[] newArray(int size) {
                        return new SavedDotViewState[size];
                    }
                };
    }

    private static class DotDrawing implements Parcelable{
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

        public void setPaintIndex(int paintIndex) {
            mPaintIndex = paintIndex;
        }

        public int getPaintIndex() {
            return mPaintIndex;
        }

        private Rect getRect() {
            Rect rect = new Rect();
            rect.set((int) (mCenterX - mRadius),
                     (int) (mCenterY - mRadius),
                     (int) (mCenterX + mRadius),
                     (int) (mCenterY + mRadius));
            return rect;
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

        public static final Parcelable.Creator<DotDrawing> CREATOR =
                new Parcelable.Creator<DotDrawing>() {
                    public DotDrawing createFromParcel(Parcel in) {
                        return new DotDrawing(in);
                    }
                    public DotDrawing[] newArray(int size) {
                        return new DotDrawing[size];
                    }
                };
    }

    private static final int NO_INDEX = -1;

    private int getDotIndexUnder(float x, float y) {
        // We iterate backwards since the "top" dots are at the end of the array.
        for (int i = mDotsArray.size() - 1; i >= 0; --i) {
            DotDrawing dot = mDotsArray.get(i);
            Rect rect = dot.getRect();
            if (rect.contains((int) x, (int) y)) {
                return i;
            }
        }
        return NO_INDEX;
    }

    // Custom Accessibility

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        // Always attempt to dispatch hover events to accessibility first.
        if (mDotViewTouchHelper.dispatchHoverEvent(event)) {
            return true;
        }

        return super.dispatchHoverEvent(event);
    }

    private static class DotViewTouchHelper extends ExploreByTouchHelper {

        DotView mView;

        public DotViewTouchHelper(View forView) {
            super(forView);
            mView = (DotView) forView;
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            final int index = mView.getDotIndexUnder(x, y);
            if (index == NO_INDEX) {
                return ExploreByTouchHelper.INVALID_ID;
            }
            return index;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            // May need more logic here if only some view are visible
            final int n = mView.mDotsArray.size();
            for (int i = 0; i < n; i++) {
                virtualViewIds.add(i);
            }
        }

        @Override
        protected void onPopulateEventForVirtualView(
                int virtualViewId, AccessibilityEvent event) {

            final DotDrawing dot = dotDrawingForVirtualViewId(virtualViewId);
            if (dot == null) {
                throw new IllegalArgumentException("Invalid virtual view id");
            }
            event.setContentDescription(sDotNameArray.get((dot.getPaintIndex())));
        }

        @Override
        protected void onPopulateNodeForVirtualView(
                int virtualViewId, AccessibilityNodeInfoCompat node) {
            final DotDrawing dot = dotDrawingForVirtualViewId(virtualViewId);
            if (dot == null) {
                throw new IllegalArgumentException("Invalid virtual view id");
            }

            node.setContentDescription(sDotNameArray.get((dot.getPaintIndex())));
            node.setBoundsInParent(dot.getRect());
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }

        @Override
        protected boolean onPerformActionForVirtualView(
                int virtualViewId, int action, Bundle arguments) {
            return false;
        }

        private DotDrawing dotDrawingForVirtualViewId(int id) {
            if (id >= 0 && id < mView.mDotsArray.size()) {
                return mView.mDotsArray.get(id);
            } else {
                return null;
            }
        }
    }
}
