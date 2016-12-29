package com.kyo.zoom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by wangkegang on 2016/10/19 .
 */
public class Demo extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mTextView = (TextView) findViewById(R.id.launch_txt);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mTextView.setText(getClass().getSimpleName() + " task id " + getTaskId());
        Log.d("wang", mTextView.getText().toString());
    }
}
