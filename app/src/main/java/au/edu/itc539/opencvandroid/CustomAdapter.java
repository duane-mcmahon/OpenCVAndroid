package au.edu.itc539.opencvandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by duane on 30/11/2017.
 */

public class CustomAdapter extends BaseAdapter {


    private final ArrayList mData;

    public CustomAdapter(Map<String, Integer> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, Integer> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, Integer> item = getItem(position);

        ((TextView) result.findViewById(R.id.f_label)).setText(item.getKey());

        ((ImageView) result.findViewById(R.id.f_image)).setImageResource(item.getValue());

        return result;
    }


}