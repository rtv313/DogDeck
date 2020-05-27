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

    public void addDog(int breedOne,int breedTwo,int breedThree,
                       int percentageBreedOne,int percentageBreedTwo,
                       int percentageBreedThree,int selectedBreed,String uriImage) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.BREED_ONE,breedOne);
        contentValue.put(DatabaseHelper.BREED_TWO,breedTwo);
        contentValue.put(DatabaseHelper.BREED_THREE,breedThree);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_ONE,percentageBreedOne);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_TWO,percentageBreedTwo);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_THREE,percentageBreedThree);
        contentValue.put(DatabaseHelper.SELECTED_BREED,selectedBreed);
        contentValue.put(DatabaseHelper.URI_IMAGE, uriImage);

        database.insert(DatabaseHelper.DOGS, null, contentValue);
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

    public int updateDog(int id, int selectedBreed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SELECTED_BREED, selectedBreed);
        int i = database.update(DatabaseHelper.DOGS, contentValues, DatabaseHelper.ID_DOGS + " = " + id, null);
        return i;
    }

    public void deleteDog(int id) {
        database.delete(DatabaseHelper.DOGS, DatabaseHelper.ID_DOGS + "=" + id, null);
    }
}