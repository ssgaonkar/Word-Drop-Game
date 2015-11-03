package se.android.worddrop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;


/**
 * Created by Suman on 10/21/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WORDDROP_DATABASE.db";
    public static final String USER_TABLE_NAME = "USER";
    public static final String USER_COLUMN_USERNAME = "USERNAME";
    public static final String USER_COLUMN_USER_ID = "USER_ID";
    public static final String USER_COLUMN_TOTAL_SCORE = "TOTAL_SCORE";
    public static final String POWERUP_TABLE_NAME = "POWER_UPS";
    public static final String POWERUP_COLUMN_ID = "POWERUP_ID";
    public static final String POWERUP_COLUMN_USER_ID = "USER_ID";
    public static final String POWERUP_COLUMN_SHRINKS_NUM = "SHRINKS_NUM";
    public static final String POWERUP_COLUMN_SCRAMBLES_NUM = "SCRAMBLES_NUM";
    public static final String POWERUP_COLUMN_DELETECOLORS_NUM = "DELETECOLORS_NUM";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        Log.d("DBHelper :: ", "Database created!");
    }

    public void onCreate(SQLiteDatabase sdb) {
        String createUserTableSQL = "CREATE TABLE " + USER_TABLE_NAME + "(" + USER_COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USER_COLUMN_USERNAME + " VARCHAR(30), " + USER_COLUMN_TOTAL_SCORE+ " INTEGER );";
        sdb.execSQL(createUserTableSQL);
        String createPWTableSQL = "CREATE TABLE " + POWERUP_TABLE_NAME + "(" + POWERUP_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ POWERUP_COLUMN_USER_ID + " INTEGER,"+ POWERUP_COLUMN_SHRINKS_NUM + " INTEGER, "+ POWERUP_COLUMN_SCRAMBLES_NUM + " INTEGER,"+ POWERUP_COLUMN_DELETECOLORS_NUM + " INTEGER, FOREIGN KEY("+ POWERUP_COLUMN_USER_ID+") REFERENCES "+ USER_TABLE_NAME+ "(" + USER_COLUMN_USER_ID + ") );";
        sdb.execSQL(createPWTableSQL);
        Log.d("DBHelper :: ", "Tables created!");
    }

    public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
        String dropUserTableSQL = "DROP TABLE IF EXISTS USER";
        sdb.execSQL(dropUserTableSQL);
        Log.d("DBHelper :: ", "Database upgraded!");
    }

    public boolean insertUser(String userName) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_USERNAME, userName);
        contentValues.put(USER_COLUMN_TOTAL_SCORE, 0);
        sdb.insert(USER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean setPowerupCounts()
    {
        int user_id =  getAutoIncrementId(USER_TABLE_NAME);
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POWERUP_COLUMN_USER_ID, user_id);
        contentValues.put(POWERUP_COLUMN_SHRINKS_NUM, 5);
        contentValues.put(POWERUP_COLUMN_SCRAMBLES_NUM, 5);
        contentValues.put(POWERUP_COLUMN_DELETECOLORS_NUM, 3);
        sdb.insert(POWERUP_TABLE_NAME, null, contentValues);
        return true;
    }

    public ContentValues getPowerupCounts(int userId)
    {
        ContentValues mapPowerupCount = new ContentValues();

        SQLiteDatabase sdb = this.getReadableDatabase();
        Cursor res =  sdb.rawQuery("select * from POWER_UPS where "+POWERUP_COLUMN_USER_ID+"  = "+userId, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            mapPowerupCount.put(POWERUP_COLUMN_USER_ID, userId);
            mapPowerupCount.put(POWERUP_COLUMN_SHRINKS_NUM, res.getInt(res.getColumnIndex(POWERUP_COLUMN_SHRINKS_NUM)));
            mapPowerupCount.put(POWERUP_COLUMN_SCRAMBLES_NUM, res.getInt(res.getColumnIndex(POWERUP_COLUMN_SCRAMBLES_NUM)));
            mapPowerupCount.put(POWERUP_COLUMN_DELETECOLORS_NUM, res.getInt(res.getColumnIndex(POWERUP_COLUMN_DELETECOLORS_NUM)));
            mapPowerupCount.put(POWERUP_COLUMN_ID, res.getInt(res.getColumnIndex(POWERUP_COLUMN_ID)));
            res.moveToNext();
        }
        res.close();
        return mapPowerupCount;
    }

    public int getAutoIncrementId(String tableName)
    {
        int returnId=0;
        SQLiteDatabase sdb = this.getReadableDatabase();
        Cursor res =  sdb.rawQuery( "SELECT last_insert_rowid() FROM "+ tableName+ " LIMIT 1", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            returnId= res.getInt(0);
            res.moveToNext();
        }
        res.close();
        return returnId;
    }

    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase sdb = this.getReadableDatabase();
        Cursor res =  sdb.rawQuery( "select * from USER", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_USERNAME)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public int getTotalScore(int userId)
    {
        int returnTotalScore = 0;

        SQLiteDatabase sdb = this.getReadableDatabase();
        Cursor res =  sdb.rawQuery("select * from "+USER_TABLE_NAME +" WHERE "+USER_COLUMN_USER_ID+" = '"+userId+"'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            returnTotalScore = res.getInt(res.getColumnIndex(USER_COLUMN_TOTAL_SCORE));
            res.moveToNext();
        }
        res.close();
        return returnTotalScore;
    }

    public void updateTotalScore(int totalScore, int userId)
    {
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_COLUMN_TOTAL_SCORE, totalScore);
        sdb.update(USER_TABLE_NAME, contentValues, USER_COLUMN_USER_ID + " = " + userId, null);
    }

    public void updatePowerUpCount(int powerupCount, String powerupType, int userId)
    {
        SQLiteDatabase sdb = this.getWritableDatabase();
        ContentValues contentValues = getPowerupCounts(userId);

        contentValues.put(powerupType, powerupCount);
        sdb.update(POWERUP_TABLE_NAME, contentValues, USER_COLUMN_USER_ID + " = " + userId, null);
    }

    public int getPowerupCount(String powerupType,int userId)
    {
        int powerupCount=0;
        SQLiteDatabase sdb = this.getReadableDatabase();

        Cursor res =  sdb.rawQuery("select " + powerupType + " from POWER_UPS where " + POWERUP_COLUMN_USER_ID + "  = " + userId, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            powerupCount = res.getInt(res.getColumnIndex(powerupType));
            res.moveToNext();
        }
        res.close();
        return powerupCount;
    }

    public int getUserId(String userName)
    {
        int userId = 0;

        SQLiteDatabase sdb = this.getReadableDatabase();
        Cursor res =  sdb.rawQuery("select * from "+USER_TABLE_NAME +" WHERE "+USER_COLUMN_USERNAME+" = '"+userName+"'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            userId = res.getInt(res.getColumnIndex(USER_COLUMN_USER_ID));
            res.moveToNext();
        }
        res.close();
        return userId;
    }

    public void closeConnections(){
        try {
            SQLiteDatabase db = getWritableDatabase();
            if(db != null) {
                db.close();
            }
        }catch(Exception e) {
            Log.d("DBHelper", e.getMessage());
        }
    }

}