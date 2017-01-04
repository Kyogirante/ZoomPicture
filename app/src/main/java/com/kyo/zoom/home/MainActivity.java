package com.kyo.zoom.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kyo.zoom.R;
import com.kyo.zoom.gif.ZoomGifActivity;
import com.kyo.zoom.picture.ZoomPictureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mToZoomPicPageBtn;

    private TextView mToZoomGifPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        mToZoomPicPageBtn = (TextView) findViewById(R.id.home_to_zoom_pic_page_btn);
        mToZoomGifPageBtn = (TextView) findViewById(R.id.home_to_zoom_gif_page_btn);

        mToZoomPicPageBtn.setOnClickListener(this);
        mToZoomGifPageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.home_to_zoom_pic_page_btn) {
            Intent intent = new Intent(this, ZoomPictureActivity.class);
            startActivity(intent);
        } else if(id == R.id.home_to_zoom_gif_page_btn) {
            Intent intent = new Intent(this, ZoomGifActivity.class);
            startActivity(intent);
        }
    }
}
