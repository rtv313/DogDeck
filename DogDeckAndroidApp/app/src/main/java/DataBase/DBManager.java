package DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

import Models.Dog;
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

    public Dog getDog(int id){

        String [] columns = new String[]{
                DatabaseHelper.ID_DOGS,DatabaseHelper.BREED_ONE,
                DatabaseHelper.BREED_TWO,DatabaseHelper.BREED_THREE,
                DatabaseHelper.PERCENTAGE_BREED_ONE,DatabaseHelper.PERCENTAGE_BREED_TWO,
                DatabaseHelper.PERCENTAGE_BREED_THREE,DatabaseHelper.SELECTED_BREED,
                DatabaseHelper.URI_IMAGE};

        Cursor cursor = database.query(DatabaseHelper.DOGS,columns,
                DatabaseHelper.ID_DOGS + "=" + String.valueOf(id),
                null,null, null,
                null, null);

        cursor.moveToFirst();
        return createDog(cursor);
    }

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

    public LinkedList<Dog> getDogs(){

        LinkedList <Dog> dogs = new <Dog> LinkedList();

        String [] columns = new String[]{
                DatabaseHelper.ID_DOGS,DatabaseHelper.BREED_ONE,
                DatabaseHelper.BREED_TWO,DatabaseHelper.BREED_THREE,
                DatabaseHelper.PERCENTAGE_BREED_ONE,DatabaseHelper.PERCENTAGE_BREED_TWO,
                DatabaseHelper.PERCENTAGE_BREED_THREE,DatabaseHelper.SELECTED_BREED,
                DatabaseHelper.URI_IMAGE};

        Cursor cursor = database.query(DatabaseHelper.DOGS,columns,
                null,null, null,null
                , DatabaseHelper.ID_DOGS + " DESC");

        while(cursor.moveToNext()){
            Dog dogItem = createDog(cursor);
            dogs.add(dogItem);
        }
        cursor.close();
        return dogs;
    }

    public void deleteDog(int id) {
        database.delete(DatabaseHelper.DOGS, DatabaseHelper.ID_DOGS + "=" + id, null);
    }

    private Dog createDog(Cursor cursor){

        int id_dog = cursor.getInt(0);
        int breedOneId = cursor.getInt(1);
        int breedTwoId = cursor.getInt(2);
        int breedThreeId = cursor.getInt(3);
        float percentageBreedOne = cursor.getFloat(4);
        float percentageBreedTwo = cursor.getFloat(5);
        float percentageBreedThree = cursor.getFloat(6);
        int selectedBreed = cursor.getInt(7);
        String uriImage = cursor.getString(8);

        DogData breedDogOne = getDogData(breedOneId);
        DogData breedDogTwo = getDogData(breedTwoId);
        DogData breedDogThree = getDogData(breedThreeId);
        DogData selected = null;

        if(breedOneId == selectedBreed)
            selected = breedDogOne;

        if(breedTwoId == selectedBreed)
            selected = breedDogTwo;

        if(breedThreeId == selectedBreed)
            selected = breedDogThree;


        String breedOneStr = breedDogOne.getName();
        String breedTwoStr = breedDogTwo.getName();
        String breedThreeStr = breedDogThree.getName();
        String percentageBreedOneStr = String.valueOf(percentageBreedOne).substring(0,5);
        String percentageBreedTwoStr = String.valueOf(percentageBreedTwo).substring(0,5);
        String percentageThreeStr = String.valueOf(percentageBreedThree).substring(0,5);
        String selectedBreedStr = selected.getName();

        Dog dog = new Dog(id_dog,breedOneStr,breedTwoStr,breedThreeStr,percentageBreedOneStr
                ,percentageBreedTwoStr,percentageThreeStr,uriImage,selectedBreedStr,selectedBreed,
                breedOneId,breedTwoId,breedThreeId);

        return  dog;
    }
}