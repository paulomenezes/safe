package menezes.paulo.safe.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SafeDB";
 
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE user ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "idFacebook INTEGER, "+
                "idGoogle INTEGER, "+
                "firstname TEXT, "+
                "lastname TEXT, "+
                "email TEXT, "+
                "biografy TEXT, "+
                "photo TEXT, "+
                "password TEXT, "+
                "gender TEXT, "+
                "phone TEXT)";
 
        db.execSQL(CREATE_USER_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
 
        // create fresh books table
        this.onCreate(db);
    }
}
