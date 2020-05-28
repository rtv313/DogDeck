package DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import Models.DogData;

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

    public int addDog(int breedOne,int breedTwo,int breedThree,
                       float percentageBreedOne,float percentageBreedTwo,
                       float percentageBreedThree,int selectedBreed,String uriImage) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.BREED_ONE,breedOne);
        contentValue.put(DatabaseHelper.BREED_TWO,breedTwo);
        contentValue.put(DatabaseHelper.BREED_THREE,breedThree);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_ONE,percentageBreedOne);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_TWO,percentageBreedTwo);
        contentValue.put(DatabaseHelper.PERCENTAGE_BREED_THREE,percentageBreedThree);
        contentValue.put(DatabaseHelper.SELECTED_BREED,selectedBreed);
        contentValue.put(DatabaseHelper.URI_IMAGE, uriImage);

        int id = (int)database.insert(DatabaseHelper.DOGS, null, contentValue);

        return id;
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

    public DogData getDogData(int id){
        String [] columns = new String[]{DatabaseHelper.NAME,DatabaseHelper.ORIGIN,
                                         DatabaseHelper.HEIGHT,DatabaseHelper.WEIGHT,
                                         DatabaseHelper.LIFE_SPAN,DatabaseHelper.TEMPERAMENT,
                                         DatabaseHelper.HEALTH};

        Cursor cursor = database.query(DatabaseHelper.DOG_BREEDS,columns,
                              DatabaseHelper.ID_DOGS_BREEDS + "=" + String.valueOf(id),
                               null,null, null,
                              null, null);

        if(cursor != null){
            cursor.moveToFirst();
            String name = cursor.getString(0);
            String origin = cursor.getString(1);
            String height = cursor.getString(2);
            String weight = cursor.getString(3);
            String lifeSpan = cursor.getString(4);
            String temperament = cursor.getString(5);
            String health = cursor.getString(6);
            DogData dogData = new DogData(name,origin,height,weight,lifeSpan,temperament,health);
            cursor.close();
            return dogData;
        }

        cursor.close();
        return null;
    }

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