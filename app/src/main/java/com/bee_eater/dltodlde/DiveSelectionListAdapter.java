package com.bee_eater.dltodlde;

import android.content.Context;
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
        TextView txtMainTitle;
        TextView txtSubTitle;
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
            viewHolder.txtMainTitle = (TextView) convertView.findViewById(R.id.maintitle);
            viewHolder.txtSubTitle = (TextView) convertView.findViewById(R.id.subtitle);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.chkIsSelected.setChecked(dive.isSelected);
        viewHolder.chkIsSelected.setOnClickListener(this);
        viewHolder.chkIsSelected.setTag(position);
        viewHolder.txtMainTitle.setText(dive.ID + " - " + dive.City + ": " + dive.Place);
        viewHolder.txtSubTitle.setText(dive.Divedate);
        // Return the completed view to render on screen
        return convertView;
    }

}