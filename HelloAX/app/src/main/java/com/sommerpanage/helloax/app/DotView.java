package com.sommerpanage.helloax.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class DotView extends View {

    private final int DEFAULT_DOT_COLOR = R.color.defaultDotColor;

    private float mDotRadius = R.dimen.default_dot_radius;

    private Paint mDot1Paint;
    private Paint mDot2Paint;
    private Paint mDot3Paint;

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

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DotView, defStyle, 0);

        mDotRadius = a.getDimension(R.styleable.DotView_dotRadius, mDotRadius);

        int color1 = a.getColor(R.styleable.DotView_dotColor1, DEFAULT_DOT_COLOR);
        int color2 = a.getColor(R.styleable.DotView_dotColor2, DEFAULT_DOT_COLOR);
        int color3 = a.getColor(R.styleable.DotView_dotColor3, DEFAULT_DOT_COLOR);

        mDot1Paint = getPaintForColor(color1);
        mDot2Paint = getPaintForColor(color2);
        mDot3Paint = getPaintForColor(color3);
    }

    private Paint getPaintForColor(int color) {
        Paint paint = new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: draw!

    }
}
