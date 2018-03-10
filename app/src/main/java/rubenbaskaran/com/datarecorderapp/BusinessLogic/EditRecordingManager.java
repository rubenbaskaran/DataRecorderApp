package rubenbaskaran.com.datarecorderapp.BusinessLogic;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import rubenbaskaran.com.datarecorderapp.DataAccess.DatabaseManager;
import rubenbaskaran.com.datarecorderapp.Fragments.SavedRecordingsFragment;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 20-07-2017.
 */

public class EditRecordingManager
{
    private int position;

    public EditRecordingManager(int position)
    {
        this.position = position;
    }

    public void EditTitle(TextView title, String timestamp, Button editTitleBtn, Context context)
    {
        if (!title.isEnabled())
        {
            title.setEnabled(true);
            editTitleBtn.setText("Save changes");
        }
        else if (title.isEnabled())
        {
            title.setEnabled(false);
            editTitleBtn.setText("Edit title");
            DatabaseManager dbMgr = new DatabaseManager(context);
            dbMgr.EditRecordingTitle(title.getText().toString(), timestamp);
        }
    }

    public void DeleteRecording(Context context, FragmentTransaction fragmentTransaction, String Title, String Filepath, String Timestamp)
    {
        DatabaseManager dbMgr = new DatabaseManager(context);
        dbMgr.DeleteRecording(Timestamp);

        File file = new File(Filepath);
        boolean result = false;

        try
        {
            result = file.delete();
        }
        catch (Exception e)
        {
            Log.e("Error", e.toString());
            Toast.makeText(context, "Failed to delete " + Title, Toast.LENGTH_LONG).show();
        }

        if (result)
        {
            Toast.makeText(context, Title + " has been deleted", Toast.LENGTH_LONG).show();
            FragmentTransaction tx = fragmentTransaction;
            SavedRecordingsFragment savedRecordingsFragment = new SavedRecordingsFragment();
            tx.replace(R.id.layout_holder, savedRecordingsFragment);
            tx.commit();
        }
        else
            Toast.makeText(context, "Failed to delete " + Title, Toast.LENGTH_LONG).show();
    }
}
