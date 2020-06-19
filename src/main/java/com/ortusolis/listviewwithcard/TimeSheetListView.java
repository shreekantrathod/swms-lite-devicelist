package com.ortusolis.listviewwithcard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeSheetListView extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] time;
    private final String[] weekDaysText;

    public TimeSheetListView(Activity context, String[] time, String[] weekdaysText) {
        super(context, R.layout.time_sheet_switch, time);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.time=time;
        this.weekDaysText=weekdaysText;


    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.time_sheet_switch, null,true);

        TextView timeText = (TextView) rowView.findViewById(R.id.time);
        TextView weekDaysList = (TextView) rowView.findViewById(R.id.weekDayText);

        timeText.setText(time[position]);
        weekDaysList.setText(weekDaysText[position]);

        return rowView;

    };
}
