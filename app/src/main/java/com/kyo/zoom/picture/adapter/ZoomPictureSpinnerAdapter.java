package com.kyo.zoom.picture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kyo.zoom.R;

/**
 * Created by wangkegang on 2016/10/19 .
 */
public class ZoomPictureSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String [] mStringArray;
    private int padding;

    public ZoomPictureSpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mStringArray = objects;
        this.padding = (int)mContext.getResources().getDimension(R.dimen.z_common_padding);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.view_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.spinner_item_text);
        tv.setText(mStringArray[position]);
        tv.setPadding(padding, padding, padding, padding);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.view_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.spinner_item_text);
        tv.setText(mStringArray[position]);
        tv.setPadding(padding/2, padding/4, padding/4, padding/2);
        return convertView;
    }
}
