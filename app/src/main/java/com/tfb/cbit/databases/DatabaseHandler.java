package com.tfb.cbit.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.utility.PrintLog;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Cbit.db";
    private static final String TBL_NAME = "upcomingContest";
    private static final String id = "id";
    private static final String CONTEST_DATE_TIME = "contest_date_time";
    private static final String CONTEST_ENABLE = "contest_enable";

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE ="CREATE TABLE "+TBL_NAME+
                "("+id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+CONTEST_DATE_TIME+
                " TEXT,"+CONTEST_ENABLE+" INTEGER )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
        onCreate(db);
    }

    public long addContest(UpcomingContestModel upcomingContestModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONTEST_DATE_TIME,upcomingContestModel.getContestDateTime());
        values.put(CONTEST_ENABLE,upcomingContestModel.getIsEnable());
        long id = db.insert(TBL_NAME, null, values);

        db.close();
        PrintLog.e(TAG,id+" addUpcoming Contest");
        return id;
    }

    public ArrayList<UpcomingContestModel> getAllContest(){
        ArrayList<UpcomingContestModel> ucm = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "select * from "+TBL_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if(cursor.moveToFirst()){
            do{
                UpcomingContestModel upcomingContestModel = new UpcomingContestModel();
                upcomingContestModel.setId(cursor.getInt(cursor.getColumnIndex(id)));
                upcomingContestModel.setContestDateTime(cursor.getString(cursor.getColumnIndex(CONTEST_DATE_TIME)));
                upcomingContestModel.setIsEnable(cursor.getInt(cursor.getColumnIndex(CONTEST_ENABLE)));
                ucm.add(upcomingContestModel);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ucm;
    }

    public int deleteItem(String tblId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int row=db.delete(TBL_NAME,id+"=?",new String[]{tblId});
        return row;
    }

    public  int getRowCount()
    {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.query(TBL_NAME, null, null, null, null, null, null);
        rowCount = cursor.getCount();
        cursor.close();
        db.close();
        return rowCount;
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TBL_NAME);
    }
}
