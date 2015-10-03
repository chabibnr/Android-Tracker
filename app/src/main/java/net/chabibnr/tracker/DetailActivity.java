package net.chabibnr.tracker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 17/08/2015.
 */
public class DetailActivity extends AppCompatActivity {
    MainModel.Data model;
    ListView trackingDetail;
    Info info;
    InfoAdapter infoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        info = new Info();

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("AWB_NO") != null) {

            String AWB_NO = bundle.getString("AWB_NO");
            model = (new MainModel(this)).getDetail(AWB_NO);
            actionBar.setTitle(model.awbNo());
            viewTrackingDetail();
        }
    }

    ArrayList<HashMap<String, String>> infoTracking;
    protected void viewTrackingDetail()
    {

        infoTracking = new ArrayList<HashMap<String, String>>();
        info.setArrayList(infoTracking);
        trackingDetail = (ListView) findViewById(R.id.listInfoTracking);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                info.addMap("AWB NO", model.awbNo());
                info.addMap("ORIGIN", model.origin());
                info.addMap("DESTINATION", model.destination());
                info.addMap("COURIER", model.courierId());
                info.addMap("COURIER SERVICE", model.courierService());
                info.addMap("SHIPPER", model.shipper());
                info.addMap("CONSIGNEE", model.consignee());
                info.addMap("STATUS", model.status());
                info.addMap("LAST UPDATE", model.lastUpdate());

                infoAdapter = new InfoAdapter(DetailActivity.this, infoTracking);
                trackingDetail.setAdapter(infoAdapter);

            }
        });
        trackingDetail.setOnItemClickListener(null);

    }


}
