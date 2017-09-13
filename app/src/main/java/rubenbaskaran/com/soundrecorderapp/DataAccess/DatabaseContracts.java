package rubenbaskaran.com.soundrecorderapp.DataAccess;

import android.provider.BaseColumns;

/**
 * Created by Ruben on 16-08-2017.
 */

public class DatabaseContracts
{
    /** This class is for holding contracts as subclasses, therefore the parent class constructor is private (not needed) */
    private DatabaseContracts(){}

    public static class RecordingTable implements BaseColumns
    {
        public static final String TABLE_NAME = "recordings";
        public static final String COLUMN_NAME_FILEPATH = "filepath";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LENGTH = "length";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
