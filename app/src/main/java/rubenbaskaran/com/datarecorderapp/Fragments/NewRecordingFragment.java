package rubenbaskaran.com.datarecorderapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rubenbaskaran.com.datarecorderapp.BusinessLogic.NewRecordingManager;
import rubenbaskaran.com.datarecorderapp.BusinessLogic.RepeatListener;
import rubenbaskaran.com.datarecorderapp.Enums.DataTypes;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class NewRecordingFragment extends Fragment
{
    private NewRecordingManager newRecordingManager;
    DataTypes dataType = DataTypes.Audio;
    private Button audioButton;
    private Button motionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.new_recording_fragment, null);

        audioButton = root.findViewById(R.id.audioButton);
        audioButton.setOnClickListener(DataInputType);
        audioButton.setEnabled(false);

        motionButton = root.findViewById(R.id.motionButton);
        motionButton.setOnClickListener(DataInputType);
        motionButton.setEnabled(true);

        TextView secondsTextView = root.findViewById(R.id.seconds_text_view);
        secondsTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
        String length = String.valueOf(sharedPreferences.getInt("length", 0));
        secondsTextView.setText(length);

        Button incrementBtn = root.findViewById(R.id.increment_button);
        incrementBtn.setOnTouchListener(IncrementClickRepeatListener);

        if (length.equals("99"))
        {
            incrementBtn.setEnabled(false);
        }
        else
        {
            incrementBtn.setEnabled(true);
        }

        Button decrementBtn = root.findViewById(R.id.decrement_button);
        decrementBtn.setOnTouchListener(DecrementClickRepeatListener);

        Button recordBtn = root.findViewById(R.id.record_button);
        recordBtn.setOnClickListener(RecordClick);

        EditText recordingTitleEditView = root.findViewById(R.id.title_of_recoding_edit_text);


        Button stopBtn = root.findViewById(R.id.stop_button);
        stopBtn.setOnClickListener(StopClick);
        stopBtn.setEnabled(false);

        if (length.equals("0"))
        {
            decrementBtn.setEnabled(false);
            recordBtn.setEnabled(false);
            recordingTitleEditView.setEnabled(false);
        }
        else
        {
            decrementBtn.setEnabled(true);
            recordBtn.setEnabled(true);
            recordingTitleEditView.setEnabled(true);
        }

        newRecordingManager = new NewRecordingManager(incrementBtn, decrementBtn, recordBtn, stopBtn, secondsTextView, recordingTitleEditView, getContext(), dataType);
        return root;
    }

    View.OnClickListener RecordClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (dataType.equals(DataTypes.Audio))
            {
                newRecordingManager.RecordAudio();
            }
            else if (dataType.equals(DataTypes.Motion))
            {
                newRecordingManager.RecordMotion();
            }
        }
    };

    View.OnClickListener StopClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            newRecordingManager.Stop();
        }
    };

    RepeatListener IncrementClickRepeatListener = new RepeatListener(400, 100, new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (newRecordingManager.getCounter() == 99)
            {
                IncrementClickRepeatListener.getHandler().removeCallbacks(IncrementClickRepeatListener.getHandlerRunnable());
                return;
            }

            newRecordingManager.Increment();
        }
    });

    RepeatListener DecrementClickRepeatListener = new RepeatListener(400, 100, new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (newRecordingManager.getCounter() == 0)
            {
                DecrementClickRepeatListener.getHandler().removeCallbacks(DecrementClickRepeatListener.getHandlerRunnable());
                return;
            }

            newRecordingManager.Decrement();
        }
    });

    View.OnClickListener DataInputType = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v.getTag().equals("audio"))
            {
                audioButton.setEnabled(false);
                motionButton.setEnabled(true);
                dataType = DataTypes.Audio;
            }
            else if (v.getTag().equals("motion"))
            {
                audioButton.setEnabled(true);
                motionButton.setEnabled(false);
                dataType = DataTypes.Motion;
            }
        }
    };
}


