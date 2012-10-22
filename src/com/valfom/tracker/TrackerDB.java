package com.valfom.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class TrackerDB extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker";
    private static final String TABLE_TRACKS = "tracks";
 
    public static final String KEY_ID = "id";
    public static final String KEY_PREFIX_ID = "_id";
    public static final String KEY_LAST_ID = "last_id";
    public static final String KEY_ACTIVITY = "activity";
    public static final String KEY_DATE = "date";
    public static final String KEY_DIST = "distance";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_MAX_SPEED = "max_speed";
    public static final String KEY_AVG_SPEED = "avg_speed";
    public static final String KEY_AVG_PACE = "avg_pace";
    public static final String KEY_MAX_PACE = "max_pace";
    public static final String KEY_ALTITUDE_GAIN = "altitude_gain";
    public static final String KEY_ALTITUDE_LOSS = "altitude_loss";
 
    public TrackerDB(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
        	+ KEY_ID + " INTEGER PRIMARY KEY,"
        	+ KEY_ACTIVITY + " TEXT,"
        	+ KEY_DATE + " TEXT,"
            + KEY_DIST + " REAL," 
        	+ KEY_DURATION + " INTEGER," 
            + KEY_MAX_SPEED + " REAL," 
        	+ KEY_AVG_SPEED + " REAL," 
            + KEY_AVG_PACE + " REAL," 
            + KEY_MAX_PACE + " REAL," 
            + KEY_ALTITUDE_GAIN + " REAL," 
            + KEY_ALTITUDE_LOSS + " REAL" + ")";
        
        db.execSQL(CREATE_TRACKS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
 
        onCreate(db);
    }
 
    public void addTrack(TrackerTrack track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        
        values.put(KEY_ACTIVITY, track.getActivity());
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_DURATION, track.getDuration());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
        values.put(KEY_AVG_SPEED, track.getAvgSpeed());
        values.put(KEY_AVG_PACE, track.getAvgPace());
        values.put(KEY_MAX_PACE, track.getMaxPace());
        values.put(KEY_ALTITUDE_GAIN, track.getAltitudeGain());
        values.put(KEY_ALTITUDE_LOSS, track.getAltitudeLoss());
 
        db.insert(TABLE_TRACKS, null, values);
        db.close();
    }
 
    public TrackerTrack getTrack(int id) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        String[] columns = new String[] { KEY_ID, KEY_ACTIVITY, KEY_DATE, 
        	KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE, 
        	KEY_MAX_PACE, KEY_ALTITUDE_GAIN, KEY_ALTITUDE_LOSS };
        String[] selectionArgs = new String[] { String.valueOf(id) };
        
        Cursor cursor = db.query(TABLE_TRACKS, columns, KEY_ID + "=?",
            selectionArgs, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        TrackerTrack track = new TrackerTrack(Integer.parseInt(cursor.getString(0)),
        	cursor.getString(1),
        	cursor.getString(2), 
        	cursor.getFloat(3), 
        	cursor.getLong(4), 
        	cursor.getFloat(5), 
        	cursor.getDouble(6), 
        	cursor.getDouble(7),
        	cursor.getDouble(8),
        	cursor.getDouble(9),
        	cursor.getDouble(10));
        
        cursor.close();
        db.close();
        
        return track;
    }
 
    public Cursor getAllTracks() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = new String[] { KEY_ID + " AS " + KEY_PREFIX_ID, KEY_ACTIVITY, KEY_DATE, 
            	KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE, 
            	KEY_MAX_PACE, KEY_ALTITUDE_GAIN, KEY_ALTITUDE_LOSS };
        
        Cursor c = db.query(TABLE_TRACKS, columns, null, null, null, null, null);
        
        return c;
    }
 
    public int updateTrack(TrackerTrack track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();    
        
        values.put(KEY_ACTIVITY, track.getActivity());
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_DURATION, track.getDuration());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
        values.put(KEY_AVG_SPEED, track.getAvgSpeed());
        values.put(KEY_AVG_PACE, track.getAvgPace());
        values.put(KEY_MAX_PACE, track.getMaxPace());
        values.put(KEY_ALTITUDE_GAIN, track.getAltitudeGain());
        values.put(KEY_ALTITUDE_LOSS, track.getAltitudeLoss());
 
        return db.update(TABLE_TRACKS, values, KEY_ID + " = ?",
        	new String[] { String.valueOf(track.getId()) });
    }
    
    public void deleteTrack(int id) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
    public int getLastTrackId() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT " + KEY_ID +" AS " + KEY_LAST_ID +" FROM " + TABLE_TRACKS + " ORDER BY " + KEY_ID +" DESC LIMIT 1";
        
        Cursor c = db.rawQuery(query, null);
        
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