package com.valfom.sports.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class TrackerDB extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_NAME = "tracker";
    
    private static final String TABLE_TRACKS = "tracks";
    private static final String TABLE_ROUTES = "routes";
    private static final String TABLE_MARKERS = "markers";
 
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
    public static final String KEY_MAX_ALTITUDE = "max_altitude";
    public static final String KEY_MIN_ALTITUDE = "min_altitude";
    
    // "routes" table
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGTITUDE = "longtitude";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_TRACK_ID = "track_id";
    
    // "markers" table
    public static final String KEY_MARKER_ID = "id";
    public static final String KEY_MARKER_LATITUDE = "latitude";
    public static final String KEY_MARKER_LONGTITUDE = "longtitude";
    public static final String KEY_MARKER_TITLE = "title";
    public static final String KEY_MARKER_MSG = "msg";
    public static final String KEY_MARKER_TRACK_ID = "track_id";
 
    public TrackerDB(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
        	+ KEY_ID + " INTEGER PRIMARY KEY,"
        	+ KEY_ACTIVITY + " INTEGER,"
        	+ KEY_DATE + " TEXT,"
            + KEY_DIST + " REAL," 
        	+ KEY_DURATION + " INTEGER," 
            + KEY_MAX_SPEED + " REAL," 
        	+ KEY_AVG_SPEED + " REAL," 
            + KEY_AVG_PACE + " REAL," 
            + KEY_MAX_PACE + " REAL," 
            + KEY_ALTITUDE_GAIN + " REAL," 
            + KEY_ALTITUDE_LOSS + " REAL,"
            + KEY_MAX_ALTITUDE + " REAL," 
            + KEY_MIN_ALTITUDE + " REAL" + ")";
        
        db.execSQL(CREATE_TRACKS_TABLE);
        
        String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
            	+ KEY_ID + " INTEGER PRIMARY KEY,"
            	+ KEY_LATITUDE + " INTEGER,"
            	+ KEY_LONGTITUDE + " INTEGER,"
            	+ KEY_SPEED + " INTEGER,"
            	+ KEY_ALTITUDE + " INTEGER,"
                + KEY_TRACK_ID + " INTEGER" + ")";
            
        db.execSQL(CREATE_ROUTES_TABLE);
        
        String CREATE_MARKERS_TABLE = "CREATE TABLE " + TABLE_MARKERS + "("
            	+ KEY_MARKER_ID + " INTEGER PRIMARY KEY,"
            	+ KEY_MARKER_LATITUDE + " INTEGER,"
            	+ KEY_MARKER_LONGTITUDE + " INTEGER,"
            	+ KEY_MARKER_TITLE + " TEXT,"
            	+ KEY_MARKER_MSG + " TEXT,"
                + KEY_MARKER_TRACK_ID + " INTEGER" + ")";
            
        db.execSQL(CREATE_MARKERS_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
 
        onCreate(db);
    }
    
    // Markers
    
    public Integer addMarker(TrackerMarker marker) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        
        values.put(KEY_MARKER_LATITUDE, marker.getLatitude());
        values.put(KEY_MARKER_LONGTITUDE, marker.getLongtitude());
        values.put(KEY_MARKER_TITLE, marker.getTitle());
        values.put(KEY_MARKER_MSG, marker.getMsg());
        values.putNull(KEY_MARKER_TRACK_ID);
 
        db.insert(TABLE_MARKERS, null, values);
        
        String query = "SELECT " + KEY_MARKER_ID +" AS " + KEY_LAST_ID + " FROM " + TABLE_MARKERS + " ORDER BY " + KEY_MARKER_ID + " DESC LIMIT 1";
        
        Cursor c = db.rawQuery(query, null);
        
        if (c.getCount() > 0) {
        	
        	c.moveToFirst();
        	
        	int id = c.getInt(c.getColumnIndex(KEY_LAST_ID));
        	
        	c.close();
        	
        	return id;
        	
        } else return null;
    }
    
    public void deleteMarker(int markerId) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_MARKERS, KEY_MARKER_ID + " = ?", new String[] { String.valueOf(markerId) });
        
        db.close();
    }
 
    public void clearMarkers() {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	db.delete(TABLE_MARKERS, KEY_MARKER_TRACK_ID + " IS NULL", null);
    	db.close();
    }

    public void saveMarkers(int trackId) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
        
        values.put(KEY_MARKER_TRACK_ID, trackId);
        
    	db.update(TABLE_MARKERS, values, KEY_MARKER_TRACK_ID + " IS NULL", null);
    	
    	db.close();
    }
    
    public void deleteMarkers(int trackId) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_MARKERS, KEY_TRACK_ID + " = ?", new String[] { String.valueOf(trackId) });
        db.close();
    }
    
    public Cursor getAllMarkers(int trackId) {
    
    	SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = new String[] { KEY_MARKER_ID, KEY_MARKER_LATITUDE, KEY_MARKER_LONGTITUDE, 
        		KEY_MARKER_TITLE, KEY_MARKER_MSG };
        String[] selectionArgs = new String[] { String.valueOf(trackId) };
        
        Cursor c = db.query(TABLE_MARKERS, columns, KEY_MARKER_TRACK_ID + "=?", selectionArgs, null, null, null);
        
        return c;
	}
    
    public Cursor getUnsavedMarkers() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT " + KEY_MARKER_ID + "," + KEY_MARKER_LATITUDE + ","
        			+ KEY_MARKER_LONGTITUDE + "," + KEY_MARKER_TITLE + ","
        			+ KEY_MARKER_MSG + " FROM " + TABLE_MARKERS + " WHERE " + KEY_MARKER_TRACK_ID + " IS NULL ORDER BY " + KEY_MARKER_ID + " ASC";

        Cursor cursor = db.rawQuery(query, null);
        
        return cursor;
    }
    
    // Tracks
    
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
        values.put(KEY_MAX_ALTITUDE, track.getMaxAltitude());
        values.put(KEY_MIN_ALTITUDE, track.getMinAltitude());
 
        db.insert(TABLE_TRACKS, null, values);
        db.close();
    }
 
    public TrackerTrack getTrack(int id) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        String[] columns = new String[] { KEY_ID, KEY_ACTIVITY, KEY_DATE, 
        		KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE, 
        		KEY_MAX_PACE, KEY_ALTITUDE_GAIN, KEY_ALTITUDE_LOSS, KEY_MAX_ALTITUDE, KEY_MIN_ALTITUDE };
        String[] selectionArgs = new String[] { String.valueOf(id) };
        
        Cursor cursor = db.query(TABLE_TRACKS, columns, KEY_ID + "=?",
            selectionArgs, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();
 
        TrackerTrack track = new TrackerTrack(cursor.getInt(0),
        	cursor.getInt(1),
        	cursor.getString(2), 
        	cursor.getFloat(3), 
        	cursor.getLong(4), 
        	cursor.getFloat(5), 
        	cursor.getDouble(6), 
        	cursor.getDouble(7),
        	cursor.getDouble(8),
        	cursor.getDouble(9),
        	cursor.getDouble(10),
        	cursor.getDouble(11),
        	cursor.getDouble(12));
        
        cursor.close();
        db.close();
        
        return track;
    }
 
    public Cursor getAllTracks() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = new String[] { KEY_ID + " AS " + KEY_PREFIX_ID, KEY_ACTIVITY, KEY_DATE, 
            	KEY_DIST, KEY_DURATION, KEY_MAX_SPEED, KEY_AVG_SPEED, KEY_AVG_PACE, 
            	KEY_MAX_PACE, KEY_ALTITUDE_GAIN, KEY_ALTITUDE_LOSS, KEY_MAX_ALTITUDE, KEY_MIN_ALTITUDE };
        
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
        values.put(KEY_MAX_ALTITUDE, track.getMaxAltitude());
        values.put(KEY_MIN_ALTITUDE, track.getMinAltitude());
 
        return db.update(TABLE_TRACKS, values, KEY_ID + " = ?",
        	new String[] { String.valueOf(track.getId()) });
    }
    
    public void deleteTrack(int trackId) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_TRACKS, KEY_ID + " = ?", new String[] { String.valueOf(trackId) });
        db.close();
        
        deleteRoute(trackId);
        deleteMarkers(trackId);
    }
    
    public int getLastTrackId() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT " + KEY_ID +" AS " + KEY_LAST_ID + " FROM " + TABLE_TRACKS + " ORDER BY " + KEY_ID +" DESC LIMIT 1";
        
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
    
    // Points
    
    public void addPoint(TrackerPoint point) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        
        values.put(KEY_LATITUDE, point.getLatitude());
        values.put(KEY_LONGTITUDE, point.getLongtitude());
        values.put(KEY_SPEED, point.getSpeed());
        values.put(KEY_ALTITUDE, point.getAltitude());
        values.putNull(KEY_TRACK_ID);
 
        db.insert(TABLE_ROUTES, null, values);
        
        db.close();
    }
    
    // Route
    
    public TrackerRoute getRoute() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
        Cursor cursor = db.rawQuery("SELECT " + KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + " FROM "
        		+ TABLE_ROUTES + " WHERE " + KEY_TRACK_ID + " IS NULL ORDER BY " + KEY_ID + " ASC", null);
        
        TrackerRoute route = new TrackerRoute();
        
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
        
        	TrackerPoint point = new TrackerPoint();
        	
        	point.setLatitude(cursor.getInt(0));
        	point.setLongtitude(cursor.getInt(1));
        	point.setSpeed(cursor.getInt(2));
        	
        	route.addPoint(point);
        }
        
        cursor.close();
        db.close();
        
        return route;
    }
    
    public TrackerRoute getRoute(int trackId) {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
        String[] columns = new String[] { KEY_LATITUDE, KEY_LONGTITUDE, KEY_SPEED, KEY_ALTITUDE };
        String orderBy = KEY_ID + " ASC";
        String[] selectionArgs = new String[] { String.valueOf(trackId) };
        
        Cursor cursor = db.query(TABLE_ROUTES, columns, KEY_TRACK_ID + "=?",
              selectionArgs, null, null, orderBy, null);
        
        TrackerRoute route = new TrackerRoute();
        
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
        
        	TrackerPoint point = new TrackerPoint();
        	
        	point.setLatitude(cursor.getInt(0));
        	point.setLongtitude(cursor.getInt(1));
        	point.setSpeed(cursor.getInt(2));
        	point.setAltitude(cursor.getInt(3));
        	
        	route.addPoint(point);
        }
        
        cursor.close();
        db.close();
        
        return route;
    }
    
    public void saveRoute(int trackId) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
        
        values.put(KEY_TRACK_ID, trackId);
        
    	db.update(TABLE_ROUTES, values, KEY_TRACK_ID + " IS NULL", null);
    	
    	db.close();
    }
    
    public void clearRoute() {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	db.delete(TABLE_ROUTES, KEY_TRACK_ID + " IS NULL", null);
    	db.close();
    }
    
    public void deleteRoute(int trackId) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_ROUTES, KEY_TRACK_ID + " = ?", new String[] { String.valueOf(trackId) });
        db.close();
    }
}