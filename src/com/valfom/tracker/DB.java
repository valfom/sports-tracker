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
    public static final String KEY_PREFIX_ID = "_id";
    public static final String KEY_LAST_ID = "last_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DIST = "distance";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_MAX_SPEED = "max_speed";
    public static final String KEY_AVG_SPEED = "avg_speed";
    public static final String KEY_AVG_PACE = "avg_pace";
 
    public DB(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
        	+ KEY_ID + " INTEGER PRIMARY KEY," 
        	+ KEY_DATE + " TEXT,"
            + KEY_DIST + " REAL," 
        	+ KEY_DURATION + " INTEGER," 
            + KEY_MAX_SPEED + " REAL," 
        	+ KEY_AVG_SPEED + " REAL," 
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
        values.put(KEY_DURATION, track.getDuration());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
        values.put(KEY_AVG_SPEED, track.getAvgSpeed());
        values.put(KEY_AVG_PACE, track.getAvgPace());
 
        db.insert(TABLE_TRACKS, null, values);
        db.close();
    }
 
    public Track getTrack(int id) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        String[] columns = new String[] { KEY_ID, KEY_DATE, 
        	KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE };
        String[] selectionArgs = new String[] { String.valueOf(id) };
        
        Cursor cursor = db.query(TABLE_TRACKS, columns, KEY_ID + "=?",
            selectionArgs, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        Track track = new Track(Integer.parseInt(cursor.getString(0)),
        	cursor.getString(1), 
        	cursor.getFloat(2), 
        	cursor.getLong(3), 
        	cursor.getFloat(4), 
        	cursor.getDouble(5), 
        	cursor.getDouble(6));
        
        cursor.close();
        db.close();
        
        return track;
    }
 
    public Cursor getAllTracks() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = new String[] { KEY_ID + " AS " + KEY_PREFIX_ID, KEY_DATE, 
            	KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE };
        
        return db.query(TABLE_TRACKS, columns, null, null, null, null, null);
    }
 
    public int updateTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();    
        
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_DURATION, track.getDuration());
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
        
        String query = "SELECT " + KEY_ID +" AS " + KEY_LAST_ID +" FROM " + TABLE_TRACKS + " ORDER BY " + KEY_ID +" DESC LIMIT 1";
        
        Cursor c = db.rawQuery(query, null);
        
//        db.close();
        
        if (c.getCount() > 0) {
        	
        	c.moveToFirst();
        	
        	int id = c.getInt(c.getColumnIndex(KEY_LAST_ID));
        	
        	c.close();
        	
        	return id;
        	
        } else 
        	return 0;
    }
 
    public int getTracksCount() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
        String query = "SELECT " + KEY_ID + " AS " 
        		+ KEY_PREFIX_ID + " FROM " + TABLE_TRACKS;
        
        Cursor cursor = db.rawQuery(query, null);
        
        cursor.close();
        db.close();
 
        return cursor.getCount();
    }
}