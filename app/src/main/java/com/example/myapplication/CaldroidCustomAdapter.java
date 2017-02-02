package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 * Created by Ира on 14.12.2016.
 */

public class CaldroidCustomAdapter extends CaldroidGridAdapter {
    public CaldroidCustomAdapter(Context context, int month, int year,
                                 Map<String, Object> caldroidData,
                                 Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView cellView;
        cellView= (TextView)super.getView(position, convertView, parent);

        DateTime dateTime = this.datetimeList.get(position);

        DateTime selectedDate  = (DateTime) extraData.get("SELECTED_DATE");

        if (dateTime.equals(getToday())) {
            cellView.setBackgroundResource(R.drawable.unselected_day);
            cellView.setTextColor(Color.RED);
        }
        if (selectedDate!=null){
            if ((selectedDate.equals(dateTime))){
                cellView.setBackgroundResource(R.drawable.selected_day);
            }
        }

        return cellView;
    }
}
