package com.valfom.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DB extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker";
    private static final String TABLE_TRACKS = "tracks";
 
    public static final String KEY_ID = "id";
    public static final String KEY_SC_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DIST = "distance";
    public static final String KEY_TIME = "time";
    public static final String KEY_MAX_SPEED = "max_speed";
    public static final String KEY_AVG_SPEED = "avg_speed";
    public static final String KEY_AVG_PACE = "avg_pace";
 
    public DB(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
        	+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
            + KEY_DIST + " REAL," + KEY_TIME + " INTEGER," 
            + KEY_MAX_SPEED + " REAL," + KEY_AVG_SPEED + " REAL," 
            + KEY_AVG_PACE + " REAL" + ")";
        
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
        values.put(KEY_AVG_SPEED, track.getAvgSpeed());
        values.put(KEY_AVG_PACE, track.getAvgPace());
 
        db.insert(TABLE_TRACKS, null, values);
        db.close();
    }
 
    public Track getTrack(int id) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_TRACKS, new String[] { KEY_ID,
        	KEY_DATE, KEY_DIST, KEY_TIME, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE }, KEY_ID + "=?",
            new String[] { String.valueOf(id) }, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        Track track = new Track(Integer.parseInt(cursor.getString(0)),
        	cursor.getString(1), cursor.getFloat(2), 
        	cursor.getLong(3), cursor.getFloat(4), cursor.getDouble(5), cursor.getDouble(6));
        
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
        values.put(KEY_AVG_SPEED, track.getAvgSpeed());
        values.put(KEY_AVG_PACE, track.getAvgPace());
 
        return db.update(TABLE_TRACKS, values, KEY_ID + " = ?",
        	new String[] { String.valueOf(track.getId()) });
    }
    
    public void deleteTrack(int id) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
    public int getLastId() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id AS last_row_id FROM tracks ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
        	
        	c.moveToFirst();
        	return c.getInt(c.getColumnIndex("last_row_id"));
        } else {
        	
        	return 0;
        }
    }
 
    public int getTracksCount() {
    	
        String countQuery = "SELECT " + KEY_ID + " as _id," + KEY_DATE + "," + KEY_DIST + "," + KEY_TIME + "," + KEY_MAX_SPEED + " FROM " + TABLE_TRACKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        return cursor.getCount();
    }
}
