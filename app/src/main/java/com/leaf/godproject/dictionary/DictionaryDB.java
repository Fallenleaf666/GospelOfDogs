package com.leaf.godproject.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.leaf.godproject.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DictionaryDB {

    private SQLiteDatabase db;
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_ISLIKE = "islike";

    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "dictionarydatabase2.db";
    public static final String TABLE_DICTIONARY_DETAIL = "dictiondetail";
    public static final String PACKAGE_NAME = "com.leaf.godproject";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;

    private SQLiteDatabase database;
    private Context coNtext;


    public DictionaryDB(Context coNtext) { this.coNtext = coNtext; }
    public SQLiteDatabase getDatabase() { return database; }
    public void setDatabase(SQLiteDatabase database) { this.database = database; }
    public void openDatabase() {
        System.out.println(DB_PATH + "/" + DB_NAME);
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    public ArrayList<Doginfo> getAllMember(){
        ArrayList<Doginfo> doginfoList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY_DETAIL;
        db = this.getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Doginfo doginfo = new Doginfo();
                doginfo.setId(Integer.parseInt(cursor.getString(0)));
                doginfo.setType(cursor.getString(1));
                doginfo.setTitle(cursor.getString(2));
                doginfo.setContent(cursor.getString(3));
                doginfo.setLike(cursor.getString(4));

                // Adding Reminders to list
                doginfoList.add(doginfo);
            } while (cursor.moveToNext());
        }
        return doginfoList;
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {
                InputStream is = this.coNtext.getResources().openRawResource(
                        R.raw.dictionarydatabase2);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    public void closeDatabase() { this.database.close(); }

    // Getting single Reminder
    public Doginfo getMember(int id){
        db = getDatabase();
        Cursor cursor = db.query(TABLE_DICTIONARY_DETAIL, new String[]
                        {
                                KEY_ID,
                                KEY_TYPE,
                                KEY_TITLE,
                                KEY_CONTENT,
                                KEY_ISLIKE,
                        }, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Doginfo doginfo = new Doginfo(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return doginfo;
    }

    // Getting single Reminder
    public ArrayList<Doginfo> getTypeMember(String TAG){
        ArrayList<Doginfo> doginfoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY_DETAIL+" WHERE type = "+"'"+TAG+"'";
        db = this.getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Doginfo doginfo = new Doginfo();
                doginfo.setId(Integer.parseInt(cursor.getString(0)));
                doginfo.setType(cursor.getString(1));
                doginfo.setTitle(cursor.getString(2));
                doginfo.setContent(cursor.getString(3));
                doginfo.setLike(cursor.getString(4));
                // Adding Reminders to list
                doginfoList.add(doginfo);
            } while (cursor.moveToNext());
        }
        return doginfoList;
    }

    // Getting
    public ArrayList<Doginfo> getlikeMember(){
        ArrayList<Doginfo> doginfoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY_DETAIL+" WHERE islike = "+"'ture'";
        db = this.getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Doginfo doginfo = new Doginfo();
                doginfo.setId(Integer.parseInt(cursor.getString(0)));
                doginfo.setType(cursor.getString(1));
                doginfo.setTitle(cursor.getString(2));
                doginfo.setContent(cursor.getString(3));
                doginfo.setLike(cursor.getString(4));
                // Adding Reminders to list
                doginfoList.add(doginfo);
            } while (cursor.moveToNext());
        }
        return doginfoList;
    }

    // Getting
    public ArrayList<Doginfo> getsearchMember(String search){
        ArrayList<Doginfo> doginfoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY_DETAIL+" WHERE title  LIKE '%"+ search+"%' OR content LIKE '%"+ search+"%'";
        db = this.getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                Doginfo doginfo = new Doginfo();
                doginfo.setId(Integer.parseInt(cursor.getString(0)));
                doginfo.setType(cursor.getString(1));
                doginfo.setTitle(cursor.getString(2));
                doginfo.setContent(cursor.getString(3));
                doginfo.setLike(cursor.getString(4));
                // Adding doginfos to list
                doginfoList.add(doginfo);
            } while (cursor.moveToNext());
        }
        return doginfoList;
    }


    // Getting Reminders Count
    public int getMemberCount(){
        String countQuery = "SELECT * FROM " + TABLE_DICTIONARY_DETAIL;
        db = this.getDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();
        return cursor.getCount();
    }



    // Updating single Reminder
    public int updateMember(Doginfo doginfo){
        db = this.getDatabase();
        ContentValues values = new ContentValues();
//        values.put(KEY_TYPE, doginfo.getTitle());
//        values.put(KEY_TITLE , doginfo.getTitle());
//        values.put(KEY_CONTENT , doginfo.getContent());
        values.put(KEY_ISLIKE , doginfo.getLike());
        // Updating row
        return db.update(TABLE_DICTIONARY_DETAIL, values, KEY_ID + "=?",
                new String[]{String.valueOf(doginfo.getId())});
    }
}
    // Adding new Reminder 沒用到
//    public int addMember(Doginfo member){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(KEY_TYPE, member.getType());
//        values.put(KEY_TITLE , member.getTitle());
//        values.put(KEY_CONTENT , member.getContent());
//        values.put(KEY_ISLIKE , member.getLike());
//
//        // Inserting Row
//        long ID = db.insert(TABLE_DICTIONARYDETAIL, null, values);
//        db.close();
//        return (int) ID;
//    }

    // Deleting single Reminder
//    public void deleteMember(Doginfo member){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_DICTIONARYDETAIL, KEY_ID + "=?",
//                new String[]{String.valueOf(member.getId())});
//        db.close();
//    }

//}