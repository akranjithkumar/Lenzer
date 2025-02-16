package com.example.netra_ai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

class Database extends SQLiteOpenHelper{

    private Context context;
    private static final String DATABASE_NAME = "chat_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "chat_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERQUERY = "userquery";
    private static final String COLUMN_ANSWER = "answer";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query= "CREATE TABLE "+ TABLE_NAME +" ("+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_USERQUERY +" TEXT, "+ COLUMN_ANSWER +" TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int il) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);

    }

    long addrun(String userquery,String answer){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();

        cv.put(COLUMN_USERQUERY,userquery);
        cv.put(COLUMN_ANSWER,answer);
        long result=db.insert(TABLE_NAME,null,cv);

        return result;

    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

}