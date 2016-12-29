package com.kyo.zoom.picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kyo.zoom.R;
import com.kyo.zoom.picture.adapter.ZoomPictureSpinnerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by wangkegang on 2016/10/19 .
 */
public class ZoomPictureActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {

    private final List<String> IMAGE_TYPES = new ArrayList<>();
    /**
     * 选择图片请求码
     */
    private final int REQUEST_CODE_CHOOSE_PIC = 0X0001;
    /**
     * 图片高度改变
     */
    private final int CODE_HEIGHT_CHANGED = 0x0010;
    /**
     * 图片宽度改变
     */
    private final int CODE_WIDTH_CHANGED = 0X0100;
    /**
     * 恢复图片高度
     */
    private final int CODE_RECOVER_HEIGHT = 0X1000;

    private final int DELAY_TIME = 500;

    private ImageView mPicImageView;
    private TextView mPicDictTextView;
    private TextView mPicInfoTextView;
    private EditText mPicHeightEditText;
    private EditText mPicWidthEditText;
    private Switch mPicLenWidFixedSwitch;
    private Spinner mPicConfigSpinner;
    private EditText mPicQuantityEditText;
    private TextView mPicConfirmTextView;

    private ArrayAdapter<String> mSpinnerAdapter;

    /**
     * 是否选择过图片
     */
    private boolean isImageSetted = false;
    /**
     * 是否固定长宽
     */
    private boolean isFixedFw = true;
    /**
     * 图片目录
     */
    private String mPictureDirectory = "";
    /**
     * 图片Config暂时无效果
     */
    private Bitmap.Config mPictureConfig = Bitmap.Config.RGB_565;
    /**
     * 图片高度
     */
    private float mPictureHeight = 0;
    /**
     * 图片宽度
     */
    private float mPictureWidth = 0;
    /**
     * 图片质量比
     */
    private int mPictureQuantity = 100;
    /**
     * 图片类型
     */
    private String mPictureType = "jpg";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg == null) {
                return;
            }

            String heightStr = mPicHeightEditText.getText().toString();
            String widthStr = mPicWidthEditText.getText().toString();

            if(TextUtils.isEmpty(heightStr) && TextUtils.isEmpty(widthStr)) {
                mPicHeightEditText.setText(String.valueOf(mPictureHeight));
                mPicWidthEditText.setText(String.valueOf(mPictureWidth));
                return;
            }

            if(!isFixedFw) {
                return;
            }

            if(CODE_HEIGHT_CHANGED == msg.what) {
                if(TextUtils.isEmpty(heightStr)) {
                    delayedHandle(CODE_RECOVER_HEIGHT, 3 * DELAY_TIME);
                    return;
                }

                float height = Float.parseFloat(heightStr);
                float width = height * mPictureWidth / mPictureHeight;

                mPicWidthEditText.setText(String.valueOf((int)width));
            }

            if(CODE_WIDTH_CHANGED == msg.what) {
                if(TextUtils.isEmpty(widthStr)) {
                    return;
                }

                float width = Float.parseFloat(widthStr);
                float height = width * mPictureHeight / mPictureWidth;

                mPicHeightEditText.setText(String.valueOf((int)height));
            }

            if(CODE_RECOVER_HEIGHT == msg.what) {
                mPicHeightEditText.setText(String.valueOf((int)mPictureHeight));
                mPicWidthEditText.setText(String.valueOf((int)mPictureWidth));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_zoom_pic);

        initView();
        initAction();

        IMAGE_TYPES.add("jpg");
        IMAGE_TYPES.add("png");
        IMAGE_TYPES.add("jpeg");
    }

    @Override
    protected void onStop() {
        super.onStop();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.pic_image_show) {
            choosePicture();
        } else if(id == R.id.pic_image_zoom_confirm) {
            savePicture();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isFixedFw = isChecked;

        if(isFixedFw) {
            String heightStr = mPicHeightEditText.getText().toString();
            String widthStr = mPicWidthEditText.getText().toString();

            if(TextUtils.isEmpty(heightStr) && TextUtils.isEmpty(widthStr)) {
                mPicHeightEditText.setText(String.valueOf(mPictureHeight));
                mPicWidthEditText.setText(String.valueOf(mPictureWidth));
                return;
            } else if(TextUtils.isEmpty(heightStr)) {
                float width = Float.parseFloat(widthStr);
                float height = width * mPictureHeight / mPictureWidth;

                mPicHeightEditText.setText(String.valueOf((int)height));
            } else if(TextUtils.isEmpty(widthStr)) {
                float height = Float.parseFloat(heightStr);
                float width = height * mPictureWidth / mPictureHeight;

                mPicWidthEditText.setText(String.valueOf((int)width));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            mPictureConfig = Bitmap.Config.RGB_565;
        } else if(position == 1) {
            mPictureConfig = Bitmap.Config.ARGB_4444;
        } else if(position == 2) {
            mPictureConfig = Bitmap.Config.ARGB_8888;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CHOOSE_PIC){
            if(resultCode == RESULT_OK){
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if(path == null || path.isEmpty()) {
                    showErrorToast();
                }
                String url = path.get(0);
                if(TextUtils.isDigitsOnly(url)) {
                    showErrorToast();
                }


                Bitmap bitmap = BitmapFactory.decodeFile(url);
                if(bitmap != null) {
                    isImageSetted = true;
                    mPictureDirectory = url;

                    mPicDictTextView.setText(getString(R.string.z_zoom_picture_dir, url));
                    mPicInfoTextView.setVisibility(View.VISIBLE);
                    mPicInfoTextView.setText(getPictureInfo(url));
                    mPicHeightEditText.setText(String.valueOf((int)mPictureHeight));
                    mPicWidthEditText.setText(String.valueOf((int)mPictureWidth));
                    mPicQuantityEditText.setText(mPictureQuantity + "");
                    mPicImageView.setImageBitmap(bitmap);
                } else {
                    showErrorToast();
                }
            }
        }
    }


    private void initView() {
        mPicImageView = (ImageView) findViewById(R.id.pic_image_show);
        mPicDictTextView = (TextView) findViewById(R.id.pic_image_directory);
        mPicInfoTextView = (TextView) findViewById(R.id.pic_image_info);
        mPicHeightEditText = (EditText) findViewById(R.id.pic_image_height_et);
        mPicWidthEditText = (EditText) findViewById(R.id.pic_image_width_et);
        mPicLenWidFixedSwitch = (Switch) findViewById(R.id.pic_image_fixed_l_w_switch);
        mPicConfigSpinner = (Spinner) findViewById(R.id.pic_image_spinner);
        mPicQuantityEditText = (EditText) findViewById(R.id.pic_image_quantity);
        mPicConfirmTextView = (TextView) findViewById(R.id.pic_image_zoom_confirm);

        mSpinnerAdapter = new ZoomPictureSpinnerAdapter(this, R.layout.view_spinner_item, getResources().getStringArray(R.array.zoom_image_config));

        mPicConfigSpinner.setAdapter(mSpinnerAdapter);
        mPicConfigSpinner.setOnItemSelectedListener(this);
        mPicConfigSpinner.setSelection(0);
    }

    private void initAction() {
        mPicImageView.setOnClickListener(this);
        mPicConfirmTextView.setOnClickListener(this);

        mPicLenWidFixedSwitch.setOnCheckedChangeListener(this);


        mPicHeightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                delayedHandle(CODE_HEIGHT_CHANGED, DELAY_TIME);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPicWidthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                delayedHandle(CODE_WIDTH_CHANGED);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPicQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantityStr = mPicQuantityEditText.getText().toString();
                if(TextUtils.isEmpty(quantityStr)) {
                    mPictureQuantity = 100;
                    return;
                }
                mPictureQuantity = Integer.valueOf(quantityStr);
            }
        });
    }

    private String getPictureInfo(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.z_zoom_picture_info));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);

        mPictureHeight = options.outHeight;
        mPictureWidth = options.outWidth;

        String[] temp = options.outMimeType.split("/");
        if(temp != null
                && temp.length == 2
                && IMAGE_TYPES.contains(temp[1])) {
            mPictureType = temp[1];
        }

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

    private void delayedHandle(int code, int delay) {
        mHandler.removeCallbacksAndMessages(null);

        mHandler.sendMessageDelayed(Message.obtain(null, code, null), delay);
    }

    private void choosePicture() {
        MultiImageSelector.create(this)
                .count(1)
                .multi()
                .start(this, REQUEST_CODE_CHOOSE_PIC);
    }

    private void savePicture() {
        if(!isImageSetted) {
            Toast.makeText(this, R.string.z_toast_no_select_pic, Toast.LENGTH_SHORT).show();
            return;
        }

        String heightStr = mPicHeightEditText.getText().toString();
        String widthStr = mPicWidthEditText.getText().toString();
        float height = TextUtils.isEmpty(heightStr) ? mPictureHeight : Float.parseFloat(heightStr);
        float width = TextUtils.isEmpty(widthStr) ? mPictureWidth : Float.parseFloat(widthStr);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPictureDirectory, options);
        options.inSampleSize = calculateInSampleSize(options, (int)width, (int)height);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mPictureDirectory, options);

        saveToSDCard(bitmap);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 计算inSampleSize值
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String getPictureName() {
        if(TextUtils.isEmpty(mPictureDirectory)) {
            return System.currentTimeMillis() + "";
        }
        String[] temp = mPictureDirectory.split(File.separator);
        String name = temp[temp.length - 1];

        String[] names = name.split("\\.");
        return names[0] + "_" + System.currentTimeMillis();
    }

    private void saveToSDCard(Bitmap bitmap) {
        if(bitmap == null) {
            return;
        }
        File file = new File("/sdcard/Pictures/ZoomPic");
        if(!file.exists()) {
            file.mkdirs();
        }

        String newDirectory = "/sdcard/Pictures/ZoomPic/" + getPictureName() + "." + mPictureType;
        file = new File(newDirectory);

        try {
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, mPictureQuantity, fos);

            fos.flush();
            fos.close();

            // 发送广播
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast();
        }

    }
}
