package net.chabibnr.tracker;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;


import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    ListView listViewTracker;
    MainAdapter mainAdapter;
    ArrayList<HashMap<String, String>> listTracking = new ArrayList<HashMap<String, String>>();
    MainModel model;
    Conf config;
    Context mContext;
    FloatingActionButton searchAWB;
    RelativeLayout progressBar;
    ActionBar actionBar;
    Drawer drawerBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new Conf(this);
        model = new MainModel(this, progressBar);
        mContext = this;

        if (!isMyServiceRunning(HttpCheckerService.class)) {
            Log.d("-------", "SErvice Started");
            Intent i = new Intent(this, HttpCheckerService.class);
            startService(i);
        }

        if(HttpCheckerService.myNotificationManager != null)
            HttpCheckerService.myNotificationManager.cancelAll();

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        drawer();

        //drawerBuilder.setActionBarDrawerToggle(new ActionBarDrawerToggle());

        listViewTracker = (ListView) findViewById(R.id.listTracker);
        searchAWB = (FloatingActionButton) findViewById(R.id.searchAWB);
        progressBar = (RelativeLayout) findViewById(R.id.progressBar);

        searchAWB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //newSearchAWB();
                //searchAWB.setVisibility(View.GONE);
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        dataList();

        listViewTracker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String noAwb = ((TextView) view.findViewById(R.id.ti_awb)).getText().toString();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("AWB_NO", noAwb);
                startActivity(intent);
            }
        });
    }

    public void dataList(){
        dataList(null);
    }

    public void dataList(String where){
        /* GEt Tracking data */
        if(listTracking != null)
            listTracking.clear();

        listTracking = model.getSimpleData(where);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainAdapter = new MainAdapter(MainActivity.this, listTracking);
                listViewTracker.setAdapter(mainAdapter);
            }
        });
    }

    @Override
    public void onResume(){
        if(HttpCheckerService.myNotificationManager != null)
            HttpCheckerService.myNotificationManager.cancelAll();

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            if (drawerBuilder != null && drawerBuilder.isDrawerOpen()) {
                drawerBuilder.closeDrawer();
            }else {
                if(drawerBuilder != null)
                    drawerBuilder.openDrawer();
            }
        }
        if (id == R.id.action_reload) {
            model.getDataFromUrl(Info.INFO_URL_SERVICE, this);
            return true;
        } else if (id == R.id.action_setting_new) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawerBuilder != null && drawerBuilder.isDrawerOpen()) {
            drawerBuilder.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void drawer(){
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .buildRoundRect("TEST", ColorGenerator.MATERIAL.getColor(9), 0);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(drawable)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(config.account())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        DrawerBuilder DBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult);
        DBuilder.addDrawerItems(new PrimaryDrawerItem().withName("ALL").withIdentifier(1));
            Cursor res = model.getCourierExists();
            while (!res.isAfterLast()) {
                try {
                    String courier = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_COURIER_ID));
                    DBuilder.addDrawerItems(new PrimaryDrawerItem().withName(courier).withIdentifier(1));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                res.moveToNext();
            }
        DBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                String textCourier = "ALL";
                if (drawerItem instanceof Nameable) {
                    textCourier = ((Nameable) drawerItem).getName().getText(mContext);
                }

                if("ALL".equals(textCourier)){
                    dataList();
                } else {
                    dataList(DataDBHelper.COLUMN_COURIER_ID+"='"+textCourier+"'");
                }
                return false;
            }
        });
        drawerBuilder = DBuilder.withActionBarDrawerToggle(false).build();
    }


    private void newSearchAWB() {

        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Cek Resi Baru")
                .setMessage("Masukan no resi.")
                .setView(input)
                .setPositiveButton("Cari", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();

                        searchAWB.setVisibility(View.VISIBLE);
                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                searchAWB.setVisibility(View.VISIBLE);
            }
        }).show()
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        searchAWB.setVisibility(View.VISIBLE);
                    }
                });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
