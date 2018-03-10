package rubenbaskaran.com.datarecorderapp.BusinessLogic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import rubenbaskaran.com.datarecorderapp.DataAccess.DatabaseManager;
import rubenbaskaran.com.datarecorderapp.Fragments.EditRecordingFragment;
import rubenbaskaran.com.datarecorderapp.Models.Recording;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 04-08-2017.
 */

public class SavedRecordingsManager
{
    private static List<Recording> recordingData;
    private Context context;
    private DatabaseManager dbMgr;

    public SavedRecordingsManager(Context context)
    {
        this.context = context;
        dbMgr = new DatabaseManager(context);
        recordingData = dbMgr.GetAllRecordings();
    }

    public List<Recording> getRecordingData()
    {
        /** Safely remove items from List<Recording> in a loop */
        Iterator<Recording> recordingIterator = recordingData.iterator();

        while (recordingIterator.hasNext())
        {
            Recording recording = recordingIterator.next();

            File file = new File(recording.getFilepath());
            if (!file.exists())
            {
                recordingIterator.remove();
                dbMgr.DeleteRecording(recording.getTimestamp());
            }
        }

        return recordingData;
    }

    public void OpenEditRecordingFragment(int position, FragmentTransaction fragmentTransaction)
    {
        File file = new File(recordingData.get(position).getFilepath());
        if (!file.exists())
        {
            Toast.makeText(context, "File doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle data = new Bundle();
        data.putString("title", recordingData.get(position).getTitle());
        data.putString("length", recordingData.get(position).getLength());
        data.putString("timestamp", recordingData.get(position).getTimestamp());
        data.putString("filepath", recordingData.get(position).getFilepath());
        data.putInt("position", position);

        FragmentTransaction tx = fragmentTransaction;
        EditRecordingFragment editRecordingFragment = new EditRecordingFragment();
        editRecordingFragment.setArguments(data);
        tx.replace(R.id.layout_holder, editRecordingFragment).addToBackStack(null);
        tx.commit();
    }
}
