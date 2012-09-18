package com.valfom.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker";
    private static final String TABLE_TRACKS = "tracks";
 
    public static final String KEY_ID = "id";
    public static final String KEY_SC_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DIST = "distance";
    public static final String KEY_TIME = "time";
    public static final String KEY_MAX_SPEED = "max_speed";
 
    public DatabaseHandler(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
        	+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
            + KEY_DIST + " REAL," + KEY_TIME + " INTEGER," 
            + KEY_MAX_SPEED + " REAL" + ")";
        
        db.execSQL(CREATE_TRACKS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
 
        onCreate(db);
    }
 
    public void addTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_TIME, track.getTime());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
 
        db.insert(TABLE_TRACKS, null, values);
        db.close();
    }
 
    public Track getTrack(int id) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_TRACKS, new String[] { KEY_ID,
        	KEY_DATE, KEY_DIST, KEY_TIME, KEY_MAX_SPEED }, KEY_ID + "=?",
            new String[] { String.valueOf(id) }, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        Track track = new Track(Integer.parseInt(cursor.getString(0)),
        	cursor.getString(1), cursor.getFloat(2), 
        	cursor.getLong(3), cursor.getFloat(4));
        
        return track;
    }
 
    public Cursor getAllTracks() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        return db.query(TABLE_TRACKS, new String[] {"id AS _id", "date", "distance", "time"}, null, null, null, null, null);
    }
 
    public int updateTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();        
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_TIME, track.getTime());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
 
        return db.update(TABLE_TRACKS, values, KEY_ID + " = ?",
        	new String[] { String.valueOf(track.getId()) });
    }
    
    public void deleteTrack(int id) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
 
    public int getTracksCount() {
    	
//        String countQuery = "SELECT  * FROM " + TABLE_TRACKS;
        String countQuery = "SELECT " + KEY_ID + " as _id," + KEY_DATE + "," + KEY_DIST + "," + KEY_TIME + "," + KEY_MAX_SPEED + " FROM " + TABLE_TRACKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        return cursor.getCount();
    }
}
