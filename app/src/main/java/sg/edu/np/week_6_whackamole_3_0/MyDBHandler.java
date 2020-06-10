package sg.edu.np.week_6_whackamole_3_0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    /*
        The Database has the following properties:
        1. Database name is WhackAMole.db
        2. The Columns consist of
            a. Username
            b. Password
            c. Level
            d. Score
        3. Add user method for adding user into the Database.
        4. Find user method that finds the current position of the user and his corresponding
           data information - username, password, level highest score for each level
        5. Delete user method that deletes based on the username
        6. To replace the data in the database, we would make use of find user, delete user and add user

        The database shall look like the following:

        Username | Password | Level | Score
        --------------------------------------
        User A   | XXX      | 1     |    0
        User A   | XXX      | 2     |    0
        User A   | XXX      | 3     |    0
        User A   | XXX      | 4     |    0
        User A   | XXX      | 5     |    0
        User A   | XXX      | 6     |    0
        User A   | XXX      | 7     |    0
        User A   | XXX      | 8     |    0
        User A   | XXX      | 9     |    0
        User A   | XXX      | 10    |    0
        User B   | YYY      | 1     |    0
        User B   | YYY      | 2     |    0

     */

    private static final String FILENAME = "MyDBHandler.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    private static final String databaseName = "WhackAMoleDB.db";
    private static final int databaseVersion = 1;
    private static final String accountsTable = "Accounts";
    private static final String accountsUsernameColumn = "Username";
    private static final String accountsPasswordColumn = "Password";
    private static final String accountsLevelColumn = "Level";
    private static final String accountsScoreColumn = "Score";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        /* HINT:
            This is used to init the database.
         */
        super(context, databaseName, factory, databaseVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /* HINT:
            This is triggered on DB creation.
            Log.v(TAG, "DB Created: " + CREATE_ACCOUNTS_TABLE);
         */
        //To be fair there should be two table, one for account data and one for level data to avoid duplicate data.
        String CREATE_USER_TABLE = "CREATE TABLE " + accountsTable +
                "(" + accountsUsernameColumn + " TEXT NOT NULL," +
                accountsPasswordColumn + " TEXT NOT NULL," +
                accountsLevelColumn + " INTEGER NOT NULL," +
                accountsScoreColumn + " INTEGER NOT NULL," +
                //Composite key (if I rmbr it correctly)
                "PRIMARY KEY (" + accountsUsernameColumn + "," + accountsLevelColumn + ")"
                + ");";
        db.execSQL(CREATE_USER_TABLE);
        Log.v(TAG, "DB Created: " + accountsTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /* HINT:
            This is triggered if there is a new version found. ALL DATA are replaced and irreversible.
         */
        //Delete table and then recreate
        db.execSQL("DROP TABLE IF EXISTS " + accountsTable);
        onCreate(db);
    }

    public void addUser(UserData userData)
    {
            /* HINT:
                This adds the user to the database based on the information given.
                Log.v(TAG, FILENAME + ": Adding data for Database: " + values.toString());
             */
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0; i<10; i++) {
            ContentValues values = new ContentValues();
            values.put(accountsUsernameColumn, userData.getMyUserName());
            values.put(accountsPasswordColumn, userData.getMyPassword());
            values.put(accountsLevelColumn, userData.getLevels().get(i));
            values.put(accountsScoreColumn, userData.getScores().get(i));

            db.insert(accountsTable, null, values);
            Log.v(TAG, FILENAME + ": Adding data for Database: " + values.toString());
        }

        db.close(); //Similiar to file in prg1, must always close db after usage
    }

    public UserData findUser(String username)
    {
        /* HINT:
            This finds the user that is specified and returns the data information if it is found.
            If not found, it will return a null.
            Log.v(TAG, FILENAME +": Find user form database: " + query);

            The following should be used in getting the query data.
            you may modify the code to suit your design.

            if(cursor.moveToFirst()){
                do{
                    ...
                    .....
                    ...
                }while(cursor.moveToNext());
                Log.v(TAG, FILENAME + ": QueryData: " + queryData.getLevels().toString() + queryData.getScores().toString());
            }
            else{
                Log.v(TAG, FILENAME+ ": No data found!");
            }
         */
        String query = "SELECT * FROM " + accountsTable + " WHERE " +
                accountsUsernameColumn  + " = \"" + username + "\"";
        //+ " ORDER BY " + ACCOUNTS_COL_LEVEL;

        Log.v(TAG, FILENAME +": Find user from database: " + query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            Log.v(TAG, FILENAME+ ": No data found!");
            return null;
        }

        String password = cursor.getString(1);
        ArrayList<Integer> levelList = new ArrayList<>();
        ArrayList<Integer> scoreList = new ArrayList<>();

        do {
            levelList.add(cursor.getInt(2));
            scoreList.add(cursor.getInt(3));
        } while (cursor.moveToNext());

        cursor.close();
        db.close();
        UserData queryData = new UserData(username, password, levelList, scoreList);
        Log.v(TAG, FILENAME + ": QueryData: " + queryData.getLevels().toString() + queryData.getScores().toString());
        return queryData;
    }

    public boolean deleteAccount(String username) {
        /* HINT:
            This finds and delete the user data in the database.
            This is not reversible.
            Log.v(TAG, FILENAME + ": Database delete user: " + query);
         */

        String query = "SELECT * FROM " + accountsTable + " WHERE " +
                accountsUsernameColumn  + " = \"" + username + "\"";

        Log.v(TAG, FILENAME + ": Database delete user: " + query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            Log.v(TAG, FILENAME+ ": No data found!");
            return false;
        }
        else {
            do {
                db.delete(accountsTable, accountsUsernameColumn + " = ?",
                        new String[]{username});
            } while (cursor.moveToNext()); //Do while is more suited here, bcus it should always run the first time

            cursor.close();
            db.close();
            return true;
        }
    }
}
