package rubenbaskaran.com.soundrecorderapp.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rubenbaskaran.com.soundrecorderapp.Models.Recording;

/**
 * Created by Ruben on 12-08-2017.
 * https://developer.android.com/training/basics/data-storage/databases.html
 */

public class DatabaseManager extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SoundRecorderAppDatabase.db";

    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Use timestamp for both primary key value and naming of the file in the filesystem
     */
    public void CreateRecording(Recording recording)
    {
        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL(SQL_DELETE_ENTRIES);
//        db.execSQL(SQL_CREATE_ENTRIES);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.RecordingTable.COLUMN_NAME_FILEPATH, recording.getFilepath());
        values.put(DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE, recording.getTitle());
        values.put(DatabaseContracts.RecordingTable.COLUMN_NAME_LENGTH, recording.getLength());
        values.put(DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP, recording.getTimestamp());

        // Insert the new row, returning the primary key value of the new row
        db.insert(DatabaseContracts.RecordingTable.TABLE_NAME, null, values);
    }

    /**
     * Remember to verify that the filepath stored in the DB actually exist in the file system,
     * before trying to play the recording. The recording could have been removed or renamed
     * through the file system by the user. In that case remove the recording from the DB.
     */
    public List<Recording> GetAllRecordings()
    {
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection =
                {
                        DatabaseContracts.RecordingTable._ID,
                        DatabaseContracts.RecordingTable.COLUMN_NAME_FILEPATH,
                        DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE,
                        DatabaseContracts.RecordingTable.COLUMN_NAME_LENGTH,
                        DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP
                };

        // Filter results WHERE "title" = 'titletest'
        //String selection = DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE + " = ?";
        //String[] selectionArgs = {"titletest"};

        // How you want the results sorted in the resulting Cursor
        //String sortOrder = DatabaseContracts.RecordingTable._ID + " ASC";

        Cursor cursor = db.query
                (
                        DatabaseContracts.RecordingTable.TABLE_NAME,    // The table to query
                        projection,                                     // The columns to return
                        null,                                           // The columns for the WHERE clause
                        null,                                           // The values for the WHERE clause
                        null,                                           // don't group the rows
                        null,                                           // don't filter by row groups
                        null                                            // The sort order
                );

        List<Recording> recordingList = new ArrayList();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContracts.RecordingTable._ID));
            String filepath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContracts.RecordingTable.COLUMN_NAME_FILEPATH));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE));
            String length = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContracts.RecordingTable.COLUMN_NAME_LENGTH));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP));

            recordingList.add(new Recording(id, filepath, title, length, timestamp));
        }
        cursor.close();

        return recordingList;
    }

    public void PrintRecordingsToLogcat(List<Recording> recordingList)
    {
        for (Recording recording : recordingList)
        {
            Log.e
                    (
                            "Id", String.valueOf(recording.getId()) +
                                    ". Filepath: " + recording.getFilepath() +
                                    ". Title: " + recording.getTitle() +
                                    ". Length: " + recording.getLength() +
                                    ". Timestamp: " + recording.getTimestamp()
                    );
        }
    }

    public void ResetDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void DeleteRecording(String timestamp)
    {
        SQLiteDatabase db = getWritableDatabase();

        // Define 'where' part of query.
        String selection = DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = {timestamp};

        // Issue SQL statement.
        db.delete(DatabaseContracts.RecordingTable.TABLE_NAME, selection, selectionArgs);
    }

    public void EditRecordingTitle(String newTitle, String timestamp)
    {
        SQLiteDatabase db = getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE, newTitle);

        // Which row to update, based on the title
        String selection = DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP+ " LIKE ?";
        String[] selectionArgs = {timestamp};

        db.update(DatabaseContracts.RecordingTable.TABLE_NAME,values,selection,selectionArgs);
    }

    //region SQL queries
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            DatabaseContracts.RecordingTable.TABLE_NAME + " (" +
            DatabaseContracts.RecordingTable._ID + " INTEGER PRIMARY KEY," +
            DatabaseContracts.RecordingTable.COLUMN_NAME_FILEPATH + " TEXT," +
            DatabaseContracts.RecordingTable.COLUMN_NAME_TITLE + " TEXT," +
            DatabaseContracts.RecordingTable.COLUMN_NAME_LENGTH + " INTEGER," +
            DatabaseContracts.RecordingTable.COLUMN_NAME_TIMESTAMP + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseContracts.RecordingTable.TABLE_NAME;
    //endregion
}
