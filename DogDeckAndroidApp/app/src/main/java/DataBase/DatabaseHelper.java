package DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Activity
    private Activity activity;

    // Tables Names
    public static final String DOG_BREEDS = "DOG_BREEDS";
    public static final String DOGS = "DOGS";

    // Table  DOG_BREEDS columns
    public static final String ID_DOGS_BREEDS = "id";
    public static final String NAME = "name";
    public static final String ORIGIN = "origin";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String LIFE_SPAN = "life_span";
    public static final String TEMPERAMENT = "temperament";
    public static final String HEALTH = "health";

    // Table DOGS columns
    public static final String ID_DOGS = "id";
    public static final String BREED_ONE = "breedOne";
    public static final String BREED_TWO = "breedTwo";
    public static final String BREED_THREE = "breedThree";
    public static final String SELECTED_BREED = "selectedBreed";
    public static final String URI_IMAGE = "uriImage";

    // Database Information
    static final String DB_NAME = "DOGS.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table DOG_BREEDS query
    private static final String CREATE_TABLE_DOGS_BREED = "create table " + DOG_BREEDS + "("
            + ID_DOGS + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + NAME + " TEXT NOT NULL,"
            + ORIGIN + " TEXT NOT NULL,"
            + HEIGHT + " TEXT NOT NULL,"
            + WEIGHT + " TEXT NOT NULL,"
            + LIFE_SPAN + " TEXT NOT NULL,"
            + TEMPERAMENT + " TEXT NOT NULL,"
            + HEALTH + " TEXT NOT NULL" + ");";

    // Creating table DOGS query
    private static final String CREATE_TABLE_DOGS= "create table " + DOGS + "("
            + ID_DOGS_BREEDS + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + BREED_ONE + " INTEGER NOT NULL,"
            + BREED_TWO + " INTEGER NOT NULL,"
            + BREED_THREE + " INTEGER NOT NULL,"
            + SELECTED_BREED + " INTEGER NOT NULL,"
            + URI_IMAGE + " TEXT NOT NULL,"
            + "FOREIGN KEY" + "(" + BREED_ONE + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\"),"
            + "FOREIGN KEY" + "(" + BREED_TWO + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\"),"
            + "FOREIGN KEY" + "(" + BREED_THREE + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\")"
            + ");";

    public DatabaseHelper(Context context,Activity activity) {
        super(context, DB_NAME, null, DB_VERSION);
        this.activity = activity;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DOGS_BREED);
        db.execSQL(CREATE_TABLE_DOGS);
        fillDogsBreedsDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DOG_BREEDS);
        db.execSQL("DROP TABLE IF EXISTS " + DOGS);
        onCreate(db);
    }

    private void fillDogsBreedsDatabase(SQLiteDatabase db){
        try{
            // Object only to get access to Assets
            InputStreamReader inputStreamReader = new InputStreamReader(this.activity.getAssets().open("dogs_clean_data.csv"));
            CSVReader reader = new CSVReader(inputStreamReader);//Specify asset file name
            String [] nextLine;
            nextLine = reader.readNext(); // Read headers

            while ((nextLine = reader.readNext()) != null) {
                int id = Integer.valueOf(nextLine[0]);
                String name = nextLine[1];
                String origin = nextLine[2];
                String height = nextLine[3].replace("\"","") + " Inches";
                String weight = nextLine[4];
                String lifeSpan = nextLine[5];
                String temperament = nextLine[6];
                String health = nextLine[7];

                ContentValues contentValue = new ContentValues();
                contentValue.put(DatabaseHelper.ID_DOGS_BREEDS,id);
                contentValue.put(DatabaseHelper.NAME, name);
                contentValue.put(DatabaseHelper.ORIGIN, origin);
                contentValue.put(DatabaseHelper.HEIGHT, height);
                contentValue.put(DatabaseHelper.WEIGHT, weight);
                contentValue.put(DatabaseHelper.LIFE_SPAN, lifeSpan);
                contentValue.put(DatabaseHelper.TEMPERAMENT, temperament);
                contentValue.put(DatabaseHelper.HEALTH, health);
                db.insert(DatabaseHelper.DOG_BREEDS, null, contentValue);
            }
            inputStreamReader.close();
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            Log.d("Fill Database Error","Failed reading the CSV");
        }
    }
}
