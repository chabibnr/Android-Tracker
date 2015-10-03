package net.chabibnr.tracker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 14/08/2015.
 */
public class MainAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public MainAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
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
            vi = inflater.inflate(R.layout.tracking_item, null);

        //ImageView image = (ImageView) vi.findViewById(R.id.shop_image); // gambar makanan
        TextView no_awb = (TextView) vi.findViewById(R.id.ti_awb);
        TextView origin = (TextView) vi.findViewById(R.id.ti_origin);
        TextView destinantion = (TextView) vi.findViewById(R.id.ti_destination);

        TextView lastUpdate = (TextView) vi.findViewById(R.id.ti_lastupdate);
        TextView status = (TextView) vi.findViewById(R.id.ti_status);
        ImageView service = (ImageView) vi.findViewById(R.id.ti_service);

        HashMap<String, String> tracking = new HashMap<String, String>();
        tracking = data.get(position);

        String imageService = tracking.get(DataDBHelper.COLUMN_COURIER_ID);

        no_awb.setText(tracking.get(DataDBHelper.COLUMN_AWB));
        origin.setText(tracking.get(DataDBHelper.COLUMN_ORIGIN));
        destinantion.setText(tracking.get(DataDBHelper.COLUMN_DESTINATION));

        lastUpdate.setText(tracking.get(DataDBHelper.COLUMN_LAST_UPDATE));
        status.setText(tracking.get(DataDBHelper.COLUMN_STATUS));

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .buildRoundRect(imageService, ColorGenerator.MATERIAL.getColor(position), 10);
        service.setImageDrawable(drawable);

        return vi;
    }
}
