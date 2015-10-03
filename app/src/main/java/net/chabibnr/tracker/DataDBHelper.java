package net.chabibnr.tracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by admin on 15/08/2015.
 */


public class DataDBHelper extends SQLiteOpenHelper{

    private static final Integer DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "tracking.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String COMMA_SEP = ",";

    public static final String TABLE_NAME = "data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AWB = "awb";
    public static final String COLUMN_COURIER_ID = "courier_id";
    public static final String COLUMN_ORIGIN = "origin";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_SHIPPER = "shipper";
    public static final String COLUMN_CONSIGNEE = "consignee";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_COURIER_SERVICE = "courier_service";
    public static final String COLUMN_LAST_UPDATE = "last_update";
    public static final String COLUMN_UI_STATUS = "ui_status";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ TABLE_NAME + "( "+
            COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_AWB + TEXT_TYPE + COMMA_SEP +
            COLUMN_COURIER_ID + TEXT_TYPE + COMMA_SEP +
            COLUMN_ORIGIN + TEXT_TYPE + COMMA_SEP +
            COLUMN_DESTINATION + TEXT_TYPE + COMMA_SEP +
            COLUMN_SHIPPER + TEXT_TYPE + COMMA_SEP +
            COLUMN_CONSIGNEE + TEXT_TYPE + COMMA_SEP +
            COLUMN_STATUS + TEXT_TYPE + COMMA_SEP +
            COLUMN_COURIER_SERVICE + TEXT_TYPE + COMMA_SEP +
            COLUMN_LAST_UPDATE + NUMERIC_TYPE + COMMA_SEP +
            COLUMN_UI_STATUS + NUMERIC_TYPE +
            " )"
            ;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    Context context;


    public DataDBHelper(Context c)
    {
        super(c, DATABASE_NAME , null, DATABASE_VERSION);
        context = c;
        //Toast.makeText(context, "On Construct", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_CREATE_ENTRIES);
        //Toast.makeText(context, "On Create", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        //Toast.makeText(context, "On Upgrade", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        //Toast.makeText(context, "On Downgrage", Toast.LENGTH_LONG).show();
    }

    /*

    public boolean insertContact  (String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    */
}


