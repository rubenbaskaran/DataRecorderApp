package rubenbaskaran.com.soundrecorderapp.BusinessLogic;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rubenbaskaran.com.soundrecorderapp.DataAccess.DatabaseManager;
import rubenbaskaran.com.soundrecorderapp.Models.Recording;

/**
 * Created by Ruben on 19-07-2017.
 */

public class NewRecordingManager
{
    //region Getters
    public int getCounter()
    {
        return counter;
    }
    //endregion

    //region Properties
    private static int counter = 0;
    private Button incrementBtn, decrementBtn, recordBtn, stopBtn;
    private TextView secondsTextView;
    private EditText recordingTitleEditView;
    private Context context;
    private String length;
    private int startLength;
    private short[] audioBuffer;
    private AudioRecord audioRecord;
    private String DateTimeNow;
    private String filepath;
    //endregion

    public NewRecordingManager(Button incrementBtn, Button decrementBtn, Button recordBtn, Button stopBtn, TextView secondsTextView, EditText recordingTitleEditView, Context context)
    {
        this.incrementBtn = incrementBtn;
        this.decrementBtn = decrementBtn;
        this.recordBtn = recordBtn;
        this.stopBtn = stopBtn;
        this.secondsTextView = secondsTextView;
        this.recordingTitleEditView = recordingTitleEditView;
        this.context = context;
    }

    public void Stop()
    {
        length = String.valueOf(startLength - counter);
        counter = 1;
    }

    public void Increment()
    {
        if (counter == 99)
            return;

        counter++;
        secondsTextView.setText(String.valueOf(counter));

        if (counter == 99)
            incrementBtn.setEnabled(false);

        if (!decrementBtn.isEnabled())
            decrementBtn.setEnabled(true);

        if (!recordBtn.isEnabled())
            recordBtn.setEnabled(true);

        if (!recordingTitleEditView.isEnabled())
            recordingTitleEditView.setEnabled(true);
    }

    public void Decrement()
    {
        if (counter == 0)
            return;

        counter--;
        secondsTextView.setText(String.valueOf(counter));

        if (counter == 0)
        {
            decrementBtn.setEnabled(false);
            recordBtn.setEnabled(false);
            recordingTitleEditView.setEnabled(false);
        }

        if (!incrementBtn.isEnabled())
            incrementBtn.setEnabled(true);
    }

    public void Record()
    {
        try
        {
            length = String.valueOf(counter);
            startLength = counter;
            audioBuffer = new short[44100 * startLength];

            if (!PrepareForRecording())
            {
                Toast.makeText(context, "Error occurred in PrepareForRecording()", Toast.LENGTH_LONG).show();
                return;
            }

            AsyncVisualDecrementation asyncVisualDecrementation = new AsyncVisualDecrementation();
            asyncVisualDecrementation.executeOnExecutor(asyncVisualDecrementation.THREAD_POOL_EXECUTOR);

            AsyncRecording asyncRecording = new AsyncRecording();
            asyncRecording.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncVisualDecrementation extends AsyncTask
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            audioRecord.startRecording();
        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            while (counter != 0 && !(counter < 0))
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    Log.e("Error", e.toString());
                }

                counter--;
                publishProgress(counter);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values)
        {
            super.onProgressUpdate(values);
            secondsTextView.setText(String.valueOf(counter));
        }

        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            audioRecord.stop();

            SaveRecordingInDb(new Recording(0, filepath, recordingTitleEditView.getText().toString(), length + " second(s)", DateTimeNow));

            counter = 0;
            incrementBtn.setEnabled(true);
            decrementBtn.setEnabled(false);
            recordBtn.setEnabled(false);
            stopBtn.setEnabled(false);
            recordingTitleEditView.setEnabled(false);
            recordingTitleEditView.setText("Set title...");
        }
    }

    private class AsyncRecording extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects)
        {
            while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
            {
                audioRecord.read(audioBuffer, 0, audioBuffer.length);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            audioRecord.release();
            audioRecord = null;
            SaveFileOnPhone();
            Toast.makeText(context, recordingTitleEditView.getText().toString() + " has been saved", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean PrepareForRecording()
    {
        incrementBtn.setEnabled(false);
        decrementBtn.setEnabled(false);
        recordBtn.setEnabled(false);
        recordingTitleEditView.setEnabled(false);
        stopBtn.setEnabled(true);

        File subdirectory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + File.separator + "Recordings");

        if (!CreateSubdirectory(subdirectory))
            return false;

        if (!GetExternalStorageState())
            return false;

        CreateFilepathAndTimestamp(subdirectory);

        if (!InitializeMediaRecorder())
            return false;

        return true;
    }

    private void SaveFileOnPhone()
    {
        File file = new File(filepath);
        StringBuilder stringBuilder = new StringBuilder();

        for (short value : audioBuffer)
        {
            stringBuilder.append(value);
            stringBuilder.append("\n");
        }

        try
        {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append(stringBuilder);
            fileWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void SaveRecordingInDb(Recording newRecording)
    {
        DatabaseManager dbMgr = new DatabaseManager(context);
        dbMgr.CreateRecording(newRecording);
    }

    //region Helper methods
    private Boolean InitializeMediaRecorder()
    {
        try
        {
            audioRecord = new AudioRecord
                    (
                            MediaRecorder.AudioSource.MIC,
                            44100,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
                    );
        }
        catch (Exception e)
        {
            Log.e("Error", e.toString());
            return false;
        }

        return true;
    }

    private void CreateFilepathAndTimestamp(File subdirectory)
    {
        DateTimeNow = GetCurrentDateAndTime();
        filepath = subdirectory + File.separator + DateTimeNow + ".txt";
    }

    private String GetCurrentDateAndTime()
    {
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateTime.format(new Date());
    }

    private boolean CreateSubdirectory(File subdirectory)
    {
        if (!subdirectory.exists())
        {
            if (!subdirectory.mkdirs())
            {
                Log.e("Error", "Couldn't create subdirectory inside Music directory");
                return false;
            }
        }

        return true;
    }

    private boolean GetExternalStorageState()
    {
        String state = Environment.getExternalStorageState(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
        if (!Environment.MEDIA_MOUNTED.equals(state))
        {
            Log.e("Error", "External storage isn't mounted");
            return false;
        }

        return true;
    }
    //endregion
}





