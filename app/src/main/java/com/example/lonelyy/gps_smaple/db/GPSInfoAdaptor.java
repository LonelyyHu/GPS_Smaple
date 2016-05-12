package com.example.lonelyy.gps_smaple.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.lonelyy.gps_smaple.model.GPSLocation;

import java.util.List;

/**
 * Created by lonelyy on 2016/5/12.
 */
public class GPSInfoAdaptor extends BaseAdapter {

    private LayoutInflater myInflater;
    private List<GPSLocation> gList;

    public GPSInfoAdaptor(Context context, List<GPSLocation> list){
        myInflater = LayoutInflater.from(context);
        this.gList = list;
    }

    @Override
    public int getCount() {
        return this.gList.size();
    }

    @Override
    public Object getItem(int position) {
        return gList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((GPSLocation)gList.get(position)).getGpsId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
