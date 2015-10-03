package net.chabibnr.tracker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.gc.materialdesign.views.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 14/08/2015.
 */
public class InfoAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public InfoAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
        Log.d("----------", "LOOP InfoAdapter");
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.info_list_item, null);

        //ImageView image = (ImageView) vi.findViewById(R.id.shop_image); // gambar makanan
        TextView infoLabel = (TextView) vi.findViewById(R.id.info_label);
        TextView infoValue = (TextView) vi.findViewById(R.id.info_value);

        HashMap<String, String> info = new HashMap<String, String>();
        info = data.get(position);

        infoLabel.setText(info.get(Info.INFO_LABEL));
        infoValue.setText(info.get(Info.INFO_VALUE));

        return vi;
    }
}
