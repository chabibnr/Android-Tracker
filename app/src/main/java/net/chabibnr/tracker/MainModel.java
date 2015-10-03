package net.chabibnr.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Createdbyadminon14/08/2015.
 */
public class MainModel {

    Context context;
    DataDBHelper dbData;

    public static final Integer STATUS_READ = 0;
    public static final Integer STATUS_UPDATE = 1;
    public static final Integer STATUS_NEW = 2;
    public static final Integer STATUS_STOP_NOTIFICATION = 3;
    RelativeLayout progressBar = null;

    public MainModel(Context c) {
        set(c);
    }

    public MainModel(Context c, RelativeLayout p){
        set(c);
        progressBar = p;
    }

    private void set(Context c){
        context = c;
        dbData = new DataDBHelper(context);
        dbData.getWritableDatabase();
    }

    public ArrayList<HashMap<String, String>> getSimpleData(){
        return getSimpleData(null);
    }

    public ArrayList<HashMap<String, String>> getSimpleData(String where) {
        if(where != null){
            where = " WHERE "+where;
        } else {
            where ="";
        }
        ArrayList<HashMap<String, String>> listTracking = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = dbData.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + DataDBHelper.TABLE_NAME + where+ " ORDER BY " + DataDBHelper.COLUMN_LAST_UPDATE + " DESC", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            try {

                HashMap<String, String> map = new HashMap<String, String>();

                String id = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_ID));
                String awb = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_AWB));
                String origin = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_ORIGIN));
                String destination = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_DESTINATION));
                String courier = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_COURIER_ID));
                String update = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_LAST_UPDATE));
                String status = res.getString(res.getColumnIndex(DataDBHelper.COLUMN_STATUS));

                map.put(DataDBHelper.COLUMN_AWB, awb);
                map.put(DataDBHelper.COLUMN_ORIGIN, origin);
                map.put(DataDBHelper.COLUMN_DESTINATION, destination);
                map.put(DataDBHelper.COLUMN_COURIER_ID, courier);
                map.put(DataDBHelper.COLUMN_LAST_UPDATE, update);
                map.put(DataDBHelper.COLUMN_STATUS, status);

                listTracking.add(map);

            } catch (Exception e) {
                e.printStackTrace();
            }


            res.moveToNext();
        }
        return listTracking;
    }

    public Cursor getCourierExists(){
        SQLiteDatabase db = dbData.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT "+DataDBHelper.COLUMN_COURIER_ID+" FROM " + DataDBHelper.TABLE_NAME + " ORDER BY " + DataDBHelper.COLUMN_COURIER_ID + " ASC ", null);
        res.moveToFirst();

        return res;
    }

    public void insertOrUpdate(JSONObject object, JSONObject shipment, Boolean isEdit) {
        Log.d("-----", "insert OR UPDATE db");
        try {

            SQLiteDatabase db = dbData.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(DataDBHelper.COLUMN_DESTINATION, object.getString("destination"));
            contentValues.put(DataDBHelper.COLUMN_ORIGIN, object.getString("origin"));
            contentValues.put(DataDBHelper.COLUMN_COURIER_ID, object.getString("courier"));
            contentValues.put(DataDBHelper.COLUMN_SHIPPER, object.getString("shipper"));
            contentValues.put(DataDBHelper.COLUMN_CONSIGNEE, object.getString("consignee"));
            contentValues.put(DataDBHelper.COLUMN_STATUS, object.getString("status"));
            contentValues.put(DataDBHelper.COLUMN_COURIER_SERVICE, object.getString("service"));
            contentValues.put(DataDBHelper.COLUMN_LAST_UPDATE, object.getString("last_update"));

            if(isEdit == false) {
                contentValues.put(DataDBHelper.COLUMN_AWB, object.getString("awb"));
                contentValues.put(DataDBHelper.COLUMN_UI_STATUS, "2");
                db.insert(DataDBHelper.TABLE_NAME, null, contentValues);
                Log.d("-----", "insert db");
            } else {
                contentValues.put(DataDBHelper.COLUMN_UI_STATUS, "1");
                db.update(DataDBHelper.TABLE_NAME, contentValues, DataDBHelper.COLUMN_AWB +"= ? ", new String[]{object.getString("awb")});
                Log.d("-----", "update db");
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void updateStatus(String awb, Integer status){

        try {
            SQLiteDatabase db = dbData.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataDBHelper.COLUMN_STATUS, status);
            db.update(DataDBHelper.TABLE_NAME, contentValues, DataDBHelper.COLUMN_AWB + "= ? ", new String[]{awb});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disableNotificaton(){

        try {
            SQLiteDatabase db = dbData.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataDBHelper.COLUMN_UI_STATUS, STATUS_STOP_NOTIFICATION);
            db.update(DataDBHelper.TABLE_NAME, contentValues, DataDBHelper.COLUMN_UI_STATUS + " IN (?,?)", new String[]{Integer.toString(STATUS_UPDATE), Integer.toString(STATUS_NEW)});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MainModel.Data getDetail(String AwbNo) {
        SQLiteDatabase db = dbData.getReadableDatabase();
        String SQLQuery = "SELECT * FROM " + DataDBHelper.TABLE_NAME + " WHERE " + DataDBHelper.COLUMN_AWB + "='" + AwbNo + "'";
        Log.d(this.getClass().getSimpleName(), "SQL " + SQLQuery);
        Cursor res = db.rawQuery(SQLQuery, null);
        res.moveToFirst();
        return new Data(res);
    }

    String Condition = null;

    public int numberOfRows(String where) {
        Condition = where;
        return numberOfRows();
    }

    public int numberOfRows() {

        SQLiteDatabase db = dbData.getReadableDatabase();
        int numRows;
        if (Condition == null)
            numRows = (int) DatabaseUtils.queryNumEntries(db, DataDBHelper.TABLE_NAME);
        else
            numRows = (int) DatabaseUtils.queryNumEntries(db, DataDBHelper.TABLE_NAME, Condition);
        Condition = null;
        return numRows;
    }

    MainActivity mainActivity = null;
    public void getDataFromUrl(String url, MainActivity a){
        mainActivity = a;
        getDataFromUrl(url);
    }

    public void getDataFromUrl(String url){

        if(progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray data = jsonObject.getJSONArray("data");
                    Integer dataLength = data.length();
                    for (int i = 0; i < dataLength; i++) {
                        JSONObject dataObject = data.getJSONObject(i);
                        String awb = dataObject.getString("awb");
                        String courier = dataObject.getString("courier");
                        Integer integer = numberOfRows(DataDBHelper.COLUMN_AWB + "='" + awb + "' AND "+ DataDBHelper.COLUMN_COURIER_ID+"='"+courier+"'");
                        if (integer > 0) {
                            insertOrUpdate(dataObject, new JSONObject(), true);
                        } else {
                            insertOrUpdate(dataObject, new JSONObject(), false);
                        }

                    }

                    if(dataLength > 0 && mainActivity != null){
                        mainActivity.dataList();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("------", "SErvice Success");
                if(progressBar!= null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);

            }

        });
        /**/
    }

    public class Data {
        Cursor cursor;

        public Data(Cursor c) {
            cursor = c;
        }

        public String awbNo() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_AWB));
        }

        public String origin() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_ORIGIN));
        }

        public String destination() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_DESTINATION));
        }

        public String courierId() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_COURIER_ID));
        }

        public String courierService() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_COURIER_SERVICE));
        }

        public String shipper() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_SHIPPER));
        }

        public String consignee() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_CONSIGNEE));
        }

        public String status() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_STATUS));
        }

        public String lastUpdate() {
            return cursor.getString(cursor.getColumnIndex(DataDBHelper.COLUMN_LAST_UPDATE));
        }
    }
}
