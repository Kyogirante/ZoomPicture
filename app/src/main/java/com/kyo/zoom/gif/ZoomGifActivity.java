package com.kyo.zoom.gif;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kyo.zoom.R;
import com.kyo.zoom.ZoomUriTransfer;

/**
 * Created by KyoWang on 2016/12/30 .
 */
public class ZoomGifActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE = 0X001;

    private GifView mGifView;
    private TextView mGifDirTv;
    private TextView mGifInfoTv;
    private EditText mWidthEt;
    private EditText mHeightEt;
    private TextView mGifConfirmBtn;

    private float mPictureHeight;
    private float mPictureWidth;
    private String mPictureType = "gif";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_zoom_gif);

        mGifView = (GifView) findViewById(R.id.zoom_gif_view);
        mGifDirTv = (TextView) findViewById(R.id.zoom_gif_directory);
        mGifInfoTv = (TextView) findViewById(R.id.zoom_gif_info);
        mGifConfirmBtn = (TextView) findViewById(R.id.zoom_gif_confirm);
        mWidthEt = (EditText) findViewById(R.id.zoom_gif_width_edit);
        mHeightEt = (EditText) findViewById(R.id.zoom_gif_height_edit);

        mGifView.setGif(R.drawable.zoom_gif_demo);

        mGifView.setOnClickListener(this);
        mGifConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.zoom_gif_view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/gif");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE);
        } else if(id == R.id.zoom_gif_confirm) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Uri uri = data.getData();

            if(uri == null) {
                showErrorToast();
            }

            String path = ZoomUriTransfer.transferUriToPath(this, uri);
            if(TextUtils.isEmpty(path)) {
                return;
            }

            mGifView.setGif(path);

            mGifDirTv.setText(getString(R.string.z_zoom_picture_dir, path));
            mGifInfoTv.setVisibility(View.VISIBLE);
            mGifInfoTv.setText(getGifInfo(path));
        }
    }

    private String getGifInfo(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.z_zoom_picture_info));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        mPictureHeight = options.outHeight;
        mPictureWidth = options.outWidth;

        sb.append(getString(R.string.z_zoom_picture_height))
                .append("-")
                .append(options.outHeight).append(" ")
                .append(getString(R.string.z_zoom_picture_width))
                .append("-")
                .append(options.outWidth).append(" ")
                .append(getString(R.string.z_zoom_picture_type))
                .append("-")
                .append(options.outMimeType);

        return sb.toString();
    }

    private void showErrorToast() {
        Toast.makeText(this, R.string.z_toast_error, Toast.LENGTH_SHORT).show();
    }
}
