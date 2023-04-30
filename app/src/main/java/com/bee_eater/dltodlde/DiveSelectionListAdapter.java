package com.bee_eater.dltodlde;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class DiveSelectionListAdapter extends ArrayAdapter<DivingLogDive> implements View.OnClickListener {

    private Context mContext;

    private static class ViewHolder {
        CheckBox chkIsSelected;
        TextView txtTitle;
        TextView txtDate;
        TextView txtDiveTime;
        TextView txtDiveDepth;
        TextView txtAddInfo;
    }

    public DiveSelectionListAdapter(Context context, List<DivingLogDive> items) {
        super(context, R.layout.layout_diveselection, items);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        CheckBox chkIsSelected = (CheckBox)v;
        Object object= getItem(position);
        DivingLogDive dive=(DivingLogDive) object;
        if (v.getId() == R.id.chkDiveSelected) {
            dive.isSelected = chkIsSelected.isChecked();
        }
    }

    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DivingLogDive dive = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_diveselection, parent, false);
            viewHolder.chkIsSelected = (CheckBox) convertView.findViewById(R.id.chkDiveSelected);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.txtAddInfo = (TextView) convertView.findViewById(R.id.txtAddInfo);
            viewHolder.txtDiveTime = (TextView) convertView.findViewById(R.id.txtDiveTime);
            viewHolder.txtDiveDepth = (TextView) convertView.findViewById(R.id.txtDiveDepth);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        // Color if already found!
        if (dive.DiveLogsIndex != -1){ // green -> already uploaded?!
            viewHolder.txtAddInfo.setTextColor(Color.GREEN);
        } else { // blue -> not uploaded yet?!
            viewHolder.txtAddInfo.setTextColor(Color.BLUE);
        }

        viewHolder.chkIsSelected.setChecked(dive.isSelected);
        viewHolder.chkIsSelected.setOnClickListener(this);
        viewHolder.chkIsSelected.setTag(position);
        viewHolder.txtTitle.setText(dive.Number + " - " + dive.City + ": " + dive.Place);
        viewHolder.txtDate.setText(dive.Divedate);
        int minutes = dive.Divetime.intValue();
        int seconds = (int)((dive.Divetime - minutes)*60);
        String dt = String.valueOf(minutes);
        dt += ":";
        if (seconds < 10)
            dt += "0";
        dt += seconds + " min";
        viewHolder.txtDiveTime.setText(dt);
        viewHolder.txtDiveDepth.setText(String.format("%.2f", dive.Depth) + " m");
        viewHolder.txtAddInfo.setText(dive.ListInfoText);

        // Return the completed view to render on screen
        return convertView;
    }

}