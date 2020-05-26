package DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Tables Names
    public static final String DOG_BREEDS = "DOG_BREEDS";
    public static final String DOGS = "DOGS";

    // Table  DOG_BREEDS columns
    public static final String ID_DOGS_BREEDS = "id";
    public static final String NAME = "name";
    public static final String ORIGIN = "origin";
    public static final String BREED = "breed";
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
    private static final String CREATE_TABLE_DOGS_BREED = "create table " + DOGS + "("
            + ID_DOGS + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + NAME + " TEXT NOT NULL,"
            + ORIGIN + "TEXT NOT NULL,"
            + BREED + "TEXT NOT NULL,"
            + HEIGHT + "TEXT NOT NULL,"
            + WEIGHT + "TEXT NOT NULL,"
            + LIFE_SPAN + "TEXT NOT NULL,"
            + TEMPERAMENT + "TEXT NOT NULL,"
            + HEALTH + "TEXT NOT NULL" + ");";

    // Creating table DOGS query
    private static final String CREATE_TABLE_DOGS= "create table " + DOG_BREEDS + "("
            + ID_DOGS_BREEDS + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + BREED_ONE + " INTEGER NOT NULL,"
            + BREED_TWO + "INTEGER NOT NULL,"
            + BREED_THREE + "INTEGER NOT NULL,"
            + SELECTED_BREED + "INTEGER NOT NULL,"
            + URI_IMAGE + "TEXT NOT NULL,"
            + "FOREIGN KEY" + "(" + BREED_ONE + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\"),"
            + "FOREIGN KEY" + "(" + BREED_TWO + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\"),"
            + "FOREIGN KEY" + "(" + BREED_THREE + ")" + " REFERENCES " + DOG_BREEDS  + "(\"id\")"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DOGS_BREED);
        db.execSQL(CREATE_TABLE_DOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DOG_BREEDS);
        db.execSQL("DROP TABLE IF EXISTS " + DOGS);
        onCreate(db);
    }
}
