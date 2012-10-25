package com.valfom.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class TrackerDB extends SQLiteOpenHelper {
 
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker";
    private static final String TABLE_TRACKS = "tracks";
    private static final String TABLE_ROUTES = "routes";
    private static final String TABLE_TMP_ROUTE = "tmp_route";
 
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
    
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGTITUDE = "longtitude";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_TRACK_ID = "track_id";
 
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
        
        String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
            	+ KEY_ID + " INTEGER PRIMARY KEY,"
            	+ KEY_LATITUDE + " INTEGER,"
            	+ KEY_LONGTITUDE + " INTEGER,"
            	+ KEY_SPEED + " INTEGER,"
            	+ KEY_ALTITUDE + " INTEGER,"
                + KEY_TRACK_ID + " INTEGER" + ")";
            
        db.execSQL(CREATE_ROUTES_TABLE);
        
        String CREATE_TMP_ROUTE_TABLE = "CREATE TABLE " + TABLE_TMP_ROUTE + "("
            	+ KEY_ID + " INTEGER PRIMARY KEY,"
            	+ KEY_LATITUDE + " INTEGER,"
            	+ KEY_LONGTITUDE + " INTEGER,"
            	+ KEY_SPEED + " INTEGER,"
            	+ KEY_ALTITUDE + " INTEGER" + ")";
            
        db.execSQL(CREATE_TMP_ROUTE_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TMP_ROUTE);
 
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
    
    //---------------------------------------------------------
    
    public void addPoint(TrackerPoint point) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        
        values.put(KEY_LATITUDE, point.getLatitude());
        values.put(KEY_LONGTITUDE, point.getLongtitude());
        values.put(KEY_SPEED, point.getSpeed());
        values.put(KEY_ALTITUDE, point.getAltitude());
 
        db.insert(TABLE_TMP_ROUTE, null, values);
        
        db.close();
    }
    
//    public TrackerPoint getPoint(int pointNum) {
//    	
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	
//    	String[] selectionArgs = new String[] { String.valueOf(pointNum - 1) };
//        
////        String query = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY " + KEY_ID + " ASC) AS row_num, " 
////        		+ KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + ", " + KEY_ALTITUDE + " FROM  " + TABLE_TMP_ROUTE 
////        		+ " ) AS point WHERE row_num =?";
//    	
////    	String query = "SELECT * FROM (SELECT (SELECT COUNT(0) FROM " + TABLE_TMP_ROUTE + " AS t1 WHERE t1.id <= t2.id) AS row_num, " 
////        		+ KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + ", " + KEY_ALTITUDE + " FROM " + TABLE_TMP_ROUTE 
////        		+ " AS t2 ORDER BY id ASC) WHERE row_num =?";
//    	
//    	String query = "SELECT " + KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + ", " + KEY_ALTITUDE + " FROM  " + TABLE_TMP_ROUTE
//    			+ " ORDER BY " + KEY_ID + " ASC LIMIT 1 OFFSET ?";
//        
//        Cursor cursor = db.rawQuery(query, selectionArgs);
//        
//        if (cursor != null)
//            cursor.moveToFirst();
////        Log.d("LALA", "cur count " + cursor.getCount() + " cols " + cursor.getColumnCount());
//        
//        TrackerPoint point = new TrackerPoint(cursor.getInt(0),
//        	cursor.getInt(1),
//        	cursor.getInt(2), 
//        	cursor.getInt(3));
//        
//        cursor.close();
//        
//    	return point;
//    }
//    
//    public TrackerPoint getPoint(int pointNum, int trackId) {
//    	
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	
//    	String[] selectionArgs = new String[] { /*String.valueOf(trackId), */ String.valueOf(trackId), String.valueOf(pointNum - 1) };
//        
////        String query = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY " + KEY_ID + " ASC) AS row_num, " 
////        		+ KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + ", " + KEY_ALTITUDE + " FROM  " + TABLE_ROUTES 
////        		+ " WHERE " + KEY_TRACK_ID + "=? ) AS point WHERE row_num =?";
//        
////        String query = "SELECT * FROM ("
////        		+ "SELECT "
////        			+ "(SELECT COUNT(0) FROM " + TABLE_ROUTES + " AS t1 WHERE " + KEY_TRACK_ID + "=? AND t1.id <= t2.id) AS row_num, " 
////        			+ KEY_LATITUDE + ", " 
////        			+ KEY_LONGTITUDE + ", " 
////        			+ KEY_SPEED + ", " 
////        			+ KEY_ALTITUDE 
////        		+ " FROM " + TABLE_ROUTES 
////        		+ " AS t2 WHERE " + KEY_TRACK_ID + "=? ORDER BY id ASC) "
////        		+ "WHERE row_num =?";
//        
//        String query = "SELECT " + KEY_LATITUDE + ", " + KEY_LONGTITUDE + ", " + KEY_SPEED + ", " + KEY_ALTITUDE + " FROM  " + TABLE_ROUTES
//        		+ " WHERE " + KEY_TRACK_ID + "=? ORDER BY " + KEY_ID + " ASC LIMIT 1 OFFSET ?";
//        
//        Cursor cursor = db.rawQuery(query, selectionArgs);
//        
//        if (cursor != null)
//            cursor.moveToFirst();
//        
////        Log.d("LALA", "cur count " + cursor.getCount() + " cols " + cursor.getColumnCount());
// 
//        TrackerPoint point = new TrackerPoint(cursor.getInt(0),
//        	cursor.getInt(1),
//        	cursor.getInt(2), 
//        	cursor.getInt(3));
//        
//        cursor.close();
//        
//    	return point;
//    }
//    
//    public int getPointsCount() {
//    	
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	 
//        Cursor cur = db.query(TABLE_TMP_ROUTE, null, null,
//                null, null, null, null, null);
//        
//        int count = cur.getCount();
//        
//        cur.close();
//        db.close();
//        
//        return count;
//    }
//    
//    public int getPointsCount(int trackId) {
//    	
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	 
//        String[] columns = new String[] { KEY_ID };
//        String[] selectionArgs = new String[] { String.valueOf(trackId) };
//        
//        Cursor cur = db.query(TABLE_ROUTES, columns, KEY_TRACK_ID + "=?",
//                selectionArgs, null, null, null, null);
//        
//        int count = cur.getCount();
//        
//        cur.close();
//        db.close();
//        
//        return count;
//    }
    
  //---------------------------------------------------------
    
    public TrackerRoute getRouteObj() {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
        String[] columns = new String[] { KEY_LATITUDE, KEY_LONGTITUDE };
        String orderBy = KEY_ID + " ASC";
        
        Cursor cursor = db.query(TABLE_TMP_ROUTE, columns, null,
            null, null, null, orderBy, null);
        
        TrackerRoute route = new TrackerRoute();
        
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
        
        	TrackerPoint point = new TrackerPoint();
        	
        	point.setLatitude(cursor.getInt(0));
        	point.setLongtitude(cursor.getInt(1));
        	
        	route.addPoint(point);
        }
        
        cursor.close();
        db.close();
        
        return route;
    }
    
    public TrackerRoute getRouteObj(int trackId) {
    	
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
    
    public Cursor getRoute() {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        String[] columns = new String[] { KEY_LATITUDE, KEY_LONGTITUDE, KEY_SPEED, KEY_ALTITUDE  };
        String orderBy = KEY_ID + " ASC";
        
        Cursor c = db.query(TABLE_TMP_ROUTE, columns, null,
            null, null, null, orderBy, null);
        
        return c;
    }
    
    public Cursor getRoute(int trackId) {
    	
        SQLiteDatabase db = this.getReadableDatabase();
 
        String[] columns = new String[] { KEY_LATITUDE, KEY_LONGTITUDE, KEY_SPEED, KEY_ALTITUDE  };
        String[] selectionArgs = new String[] { String.valueOf(trackId) };
        String orderBy = KEY_ID + " ASC";
        
        Cursor c = db.query(TABLE_ROUTES, columns, KEY_TRACK_ID + "=?",
            selectionArgs, null, null, orderBy, null);
        
//        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
        
//        }
        
        return c;
    }
    
    public void saveRoute(int trackId) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String[] columns = new String[] { KEY_LATITUDE, KEY_LONGTITUDE, KEY_SPEED, KEY_ALTITUDE  };
        String orderBy = KEY_ID + " ASC";
        
        Cursor cursor = db.query(TABLE_TMP_ROUTE, columns, null,
            null, null, null, orderBy, null);
    	
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
            
        	ContentValues values = new ContentValues();
            
            values.put(KEY_LATITUDE, cursor.getInt(0));
            values.put(KEY_LONGTITUDE, cursor.getInt(1));
            values.put(KEY_SPEED, cursor.getInt(2));
            values.put(KEY_ALTITUDE, cursor.getInt(3));
            values.put(KEY_TRACK_ID, trackId);
        	
        	db.insert(TABLE_ROUTES, null, values);
        }
        
    	db.close();
    }
    
    public void clearRoute() {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	db.delete(TABLE_TMP_ROUTE, null, null);
    	
    	db.close();
    }
    
    public void deleteRoute(int trackId) {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.delete(TABLE_ROUTES, KEY_TRACK_ID + " = ?",
                new String[] { String.valueOf(trackId) });
        db.close();
    }
    
    public void debugInfo(int num) {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor c1 = db.rawQuery("SELECT " + KEY_LATITUDE + " FROM " + TABLE_ROUTES, null);
    	Cursor c2 = db.rawQuery("SELECT " + KEY_LATITUDE + " FROM " + TABLE_TMP_ROUTE, null);
    	
    	Log.d("LALA", num + " - routes " + c1.getCount() + " tmp route " + c2.getCount());
    	
    	c1.close();
    	c2.close();
    	db.close();
    }
}