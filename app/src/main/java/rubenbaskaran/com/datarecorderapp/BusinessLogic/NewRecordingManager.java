package rubenbaskaran.com.datarecorderapp.BusinessLogic;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import rubenbaskaran.com.datarecorderapp.DataAccess.DatabaseManager;
import rubenbaskaran.com.datarecorderapp.Enums.DataTypes;
import rubenbaskaran.com.datarecorderapp.Models.Recording;

/**
 * Created by Ruben on 19-07-2017.
 */

public class NewRecordingManager
{
    //region Getters and setters
    public static void setCounter(int counter)
    {
        NewRecordingManager.counter = counter;
    }

    public static void setInterval(Integer interval)
    {
        NewRecordingManager.interval = interval;
    }

    public static void setRepeats(Integer repeats)
    {
        NewRecordingManager.repeats = repeats;
    }
    //endregion

    //region Properties
    private static int counter;
    private Button recordBtn, stopBtn;
    private EditText secondsTextView, intervalEditText, repeatsEditText;
    private EditText recordingTitleEditView;
    private Context context;
    private String length;
    private int startLength;
    private short[] audioBuffer;
    private AudioRecord audioRecord;
    private String DateTimeNow;
    private String filepath;
    private DataTypes dataType;
    private boolean recordMotionData;
    private StringBuilder motionStringBuilder = new StringBuilder();
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private TextView motionTextView;
    private static Integer interval = null;
    private static Integer repeats = null;
    public static Boolean Recording = false;
    //endregion

    //region Constructor
    public NewRecordingManager(EditText intervalEditText, EditText repeatsEditText, Button recordBtn, Button stopBtn, EditText secondsTextView, EditText recordingTitleEditView, Context context, DataTypes dataType, TextView motionTextView)
    {
        this.dataType = dataType;
        this.intervalEditText = intervalEditText;
        this.repeatsEditText = repeatsEditText;
        this.recordBtn = recordBtn;
        this.stopBtn = stopBtn;
        this.secondsTextView = secondsTextView;
        this.recordingTitleEditView = recordingTitleEditView;
        this.context = context;
        this.motionTextView = motionTextView;

        SharedPreferences sharedPreferences = context.getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
        counter = sharedPreferences.getInt("length", 0);
    }
//endregion

    //region Buttons
    public void Stop()
    {
        RecordingOff();
        Recording = false;
        length = String.valueOf(startLength - counter);
        setCounter(0);
        setRepeats(0);
    }
    //endregion

    //region Userinterface
    private void RecordingOn()
    {
        secondsTextView.setEnabled(false);
        intervalEditText.setEnabled(false);
        repeatsEditText.setEnabled(false);
        recordBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        recordingTitleEditView.setEnabled(false);
    }

    private void RecordingOff()
    {
        secondsTextView.setEnabled(true);
        intervalEditText.setEnabled(true);
        repeatsEditText.setEnabled(true);
        recordBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        recordingTitleEditView.setEnabled(true);
    }
    //endregion

    //region Audio
    public void RecordAudio(DataTypes dataType)
    {
        try
        {
            this.dataType = dataType;
            SharedPreferences sharedPreferences = context.getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("length", counter).apply();

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

            AsyncAudioRecording asyncAudioRecording = new AsyncAudioRecording();
            asyncAudioRecording.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncAudioRecording extends AsyncTask
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
        }
    }
    //endregion

    //region Motion
    public void RecordMotion(DataTypes dataType)
    {
        try
        {
            this.dataType = dataType;
            SharedPreferences sharedPreferences = context.getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("length", counter).apply();

            length = String.valueOf(counter);
            startLength = counter;

            if (!PrepareForRecording())
            {
                Toast.makeText(context, "Error occurred in PrepareForRecording()", Toast.LENGTH_LONG).show();
                return;
            }

            if (repeats == null && !repeatsEditText.getText().toString().equals("") && !repeatsEditText.getText().toString().equals("0"))
            {
                repeats = Integer.parseInt(repeatsEditText.getText().toString());

                if (interval == null && !intervalEditText.getText().toString().equals(""))
                {
                    interval = Integer.parseInt(intervalEditText.getText().toString()) * 1000;
                }
            }

            AsyncVisualDecrementation asyncVisualDecrementation = new AsyncVisualDecrementation();
            asyncVisualDecrementation.executeOnExecutor(asyncVisualDecrementation.THREAD_POOL_EXECUTOR);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void StartListeningToSensorData()
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        recordMotionData = true;

        sensorEventListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && recordMotionData == true)
                {
                    String value = String.format("%.2f", event.values[0]) + "  ,  " + String.format("%.2f", event.values[1]) + "  ,  " + String.format("%.2f", event.values[2]);
                    motionStringBuilder.append(value);
                    motionStringBuilder.append("\n");

                    motionTextView.setText(String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
            }
        };

        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //endregion

    private class AsyncVisualDecrementation extends AsyncTask
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            RecordingOn();
            Recording = true;

            if (dataType.equals(DataTypes.Audio))
            {
                audioRecord.startRecording();
            }
            else if (dataType.equals(DataTypes.Motion))
            {
                StartListeningToSensorData();
            }
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

            String title;
            if (recordingTitleEditView.getText().toString().isEmpty())
            {
                title = GetCurrentDateAndTime();
            }
            else
            {
                title = recordingTitleEditView.getText().toString();
            }

            if (dataType.equals(DataTypes.Audio))
            {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            else if (dataType.equals(DataTypes.Motion))
            {
                recordMotionData = false;
                sensorManager.unregisterListener(sensorEventListener);
            }

            SaveFileOnPhone();
            SaveRecordingInDb(new Recording(0, filepath, title, length + " second(s)", DateTimeNow));

            SharedPreferences sharedPreferences = context.getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
            counter = sharedPreferences.getInt("length", 0);
            secondsTextView.setText(String.valueOf(counter));

            if (repeats != null && repeats > 0 && Recording)
            {
                if (interval != null && interval > 0)
                {
                    try
                    {
                        Thread.sleep(interval);
                        repeats--;
                        repeatsEditText.setText(Integer.toString(repeats));
                        counter = startLength;
                        secondsTextView.setText(Integer.toString(counter));

                        if (dataType.equals(DataTypes.Motion))
                        {
                            RecordMotion(DataTypes.Motion);
                            motionStringBuilder = new StringBuilder();
                        }
                        else if (dataType.equals(DataTypes.Audio))
                        {
                            RecordAudio(DataTypes.Audio);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("Error", "Couldn't execute Thread.sleep()");
                    }
                }
                else
                {
                    repeats--;
                    repeatsEditText.setText(Integer.toString(repeats));
                    counter = startLength;
                    secondsTextView.setText(Integer.toString(counter));

                    if (dataType.equals(DataTypes.Motion))
                    {
                        RecordMotion(DataTypes.Motion);
                        motionStringBuilder = new StringBuilder();
                    }
                    else if (dataType.equals(DataTypes.Audio))
                    {
                        RecordAudio(DataTypes.Audio);
                    }
                }

                return;
            }

            RecordingOff();
            Recording = false;
            Toast.makeText(context, title + " has been saved", Toast.LENGTH_SHORT).show();
        }
    }

    //region Helper methods
    private void SaveFileOnPhone()
    {
        File file = new File(filepath);
        StringBuilder stringBuilder = null;

        if (dataType.equals(DataTypes.Audio))
        {
            stringBuilder = new StringBuilder();

            for (short value : audioBuffer)
            {
                stringBuilder.append(value);
                stringBuilder.append("\n");
            }
        }
        else if (dataType.equals(DataTypes.Motion))
        {
            stringBuilder = motionStringBuilder;
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

    private boolean PrepareForRecording()
    {
        recordBtn.setEnabled(false);
        recordingTitleEditView.setEnabled(false);
        stopBtn.setEnabled(true);

        File subdirectory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)) + File.separator + "Recordings");

        if (!CreateSubdirectory(subdirectory))
            return false;

        if (!GetExternalStorageState())
            return false;

        CreateFilepathAndTimestamp(subdirectory);

        if (dataType.equals(DataTypes.Audio))
        {
            if (!InitializeMediaRecorder())
            {
                return false;
            }
        }

        return true;
    }

    private void SaveRecordingInDb(Recording newRecording)
    {
        DatabaseManager dbMgr = new DatabaseManager(context);
        dbMgr.CreateRecording(newRecording);
    }

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