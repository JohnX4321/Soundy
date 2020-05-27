package com.thingsenz.soundy.musicplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongHelper extends SQLiteOpenHelper {

    private static final String DBNAME="allsongs.db";
    private static final int VERSION=1;
    private Context mContext;
    public SongHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(queryTocreateRecentHistory());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String queryTocreateRecentHistory() {
        return "create table "+DBSchema.RecentHistory.TABLE_NAME+ "( "+
                "_id integer primary key autoincrement,"+
                DBSchema.RecentHistory.Cols.ID + ", "+
                DBSchema.RecentHistory.Cols.TIME_PLAYED + ")";
    }

}
