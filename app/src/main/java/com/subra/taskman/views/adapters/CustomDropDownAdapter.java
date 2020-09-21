package com.subra.taskman.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomDropDownAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context ctx;
    private ArrayList<String> items;

    public CustomDropDownAdapter(Context ctx, ArrayList<String> items) {
        this.items = items;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        EditText edTxt = new EditText(ctx);
        TextView txt = new TextView(ctx);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        //txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(items.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(ctx);
        //txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        //txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
        txt.setText(items.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }
}
