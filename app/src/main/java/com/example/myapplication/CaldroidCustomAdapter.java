package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Map;

import hirondelle.date4j.DateTime;

import static com.example.myapplication.R.drawable.green_border;

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
            cellView.setBackgroundResource(R.drawable.no_border);
            cellView.setTextColor(Color.RED);
        }
        if (selectedDate!=null){
            if ((selectedDate.equals(dateTime))){
                cellView.setBackgroundResource(R.drawable.list_bcg_1);
            }
        }

        return cellView;
    }
}
