package com.sommerpanage.helloax.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    private Button mMinusButton;
    private ImageButton mPlusButton;
    private Button mClearButton;
    private DotView mDotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMinusButton = (Button) findViewById(R.id.minus_button);
        mPlusButton = (ImageButton) findViewById(R.id.plus_button);
        mClearButton = (Button) findViewById(R.id.clear_button);
        mDotView = (DotView) findViewById(R.id.dot_view);

        setupButton(mMinusButton);
        setupButton(mPlusButton);
        setupButton(mClearButton);
    }

    private void setupButton(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mMinusButton) {
                    mDotView.removeLastDot();
                } else if (v == mPlusButton) {
                    mDotView.addRandomDot();
                } else if (v == mClearButton) {
                    mDotView.clearDots();
                }
            }
        });
    }
}
