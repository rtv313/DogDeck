package DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private Activity activity;
    private SQLiteDatabase database;

    public DBManager(Context c, Activity activity) {
        context = c;
        this.activity = activity;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context,this.activity);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(int id,String name,String origin,String height,
                       String weight,String lifeSpan,String temperament,String health) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ID_DOGS_BREEDS,id);
        contentValue.put(DatabaseHelper.NAME, name);
        contentValue.put(DatabaseHelper.ORIGIN, origin);
        contentValue.put(DatabaseHelper.HEIGHT, height);
        contentValue.put(DatabaseHelper.WEIGHT, weight);
        contentValue.put(DatabaseHelper.LIFE_SPAN, lifeSpan);
        contentValue.put(DatabaseHelper.TEMPERAMENT, temperament);
        contentValue.put(DatabaseHelper.HEALTH, health);

        database.insert(DatabaseHelper.DOG_BREEDS, null, contentValue);
    }

    /*
    public Cursor fetch() {

        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
     */

    /*
    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }
     */

    /*
    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
    */
}