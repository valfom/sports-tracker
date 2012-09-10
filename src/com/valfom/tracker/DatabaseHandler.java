package com.valfom.tracker;

import java.util.ArrayList;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "tracker";
 
    // Contacts table name
    private static final String TABLE_TRACKS = "tracks";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_DIST = "distance";
    private static final String KEY_TIME = "time";
    private static final String KEY_MAX_SPEED = "max_speed";
 
    public DatabaseHandler(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_DIST + " REAL," + KEY_TIME + " INTEGER," 
                + KEY_MAX_SPEED + " REAL" + ")";
        
        db.execSQL(CREATE_TRACKS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
 
        // Create tables again
        onCreate(db);
    }
 
    // Adding new track
    public void addTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_TIME, track.getTime());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
 
        // Inserting Row
        db.insert(TABLE_TRACKS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single track
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
 
    // Getting all tracks
    public List<Track> getAllTracks() {
    	
        List<Track> trackList = new ArrayList<Track>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRACKS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Track track = new Track();
                track.setId(Integer.parseInt(cursor.getString(0)));
                track.setDate(cursor.getString(1));
                track.setDistance(cursor.getFloat(2));
                track.setTime(cursor.getLong(3));
                track.setMaxSpeed(cursor.getFloat(4));
                
                // Adding track to list
                trackList.add(track);
            } while (cursor.moveToNext());
        }
 
        // return track list
        return trackList;
    }
 
    // Updating single track
    public int updateTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();        
        values.put(KEY_DATE, track.getDate());
        values.put(KEY_DIST, track.getDistance());
        values.put(KEY_TIME, track.getTime());
        values.put(KEY_MAX_SPEED, track.getMaxSpeed());
 
        // updating row
        return db.update(TABLE_TRACKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(track.getId()) });
    }
 
    // Deleting single track
    public void deleteTrack(Track track) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                new String[] { String.valueOf(track.getId()) });
        db.close();
    }
    
    public void deleteTrack(int id) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRACKS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
 
    // Getting tracks Count
    public int getTracksCount() {
    	
        String countQuery = "SELECT  * FROM " + TABLE_TRACKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

}
