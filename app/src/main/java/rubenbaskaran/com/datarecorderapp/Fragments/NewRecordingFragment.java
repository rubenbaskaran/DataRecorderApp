package rubenbaskaran.com.datarecorderapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rubenbaskaran.com.datarecorderapp.BusinessLogic.NewRecordingManager;
import rubenbaskaran.com.datarecorderapp.Enums.DataTypes;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class NewRecordingFragment extends Fragment
{
    //region Properties
    private NewRecordingManager newRecordingManager;
    DataTypes dataType = DataTypes.Audio;
    private Button audioButton;
    private Button motionButton;
    private EditText recordingTitleEditView;
    TextView motionTextView;
    EditText secondsEditText;
    EditText intervalEditText;
    EditText repeatsEditText;
    Button recordBtn;
    Button stopBtn;
    //endregion

    private void ReadyForRecording()
    {
        secondsEditText.setEnabled(true);
        intervalEditText.setEnabled(true);
        repeatsEditText.setEnabled(true);
        recordBtn.setEnabled(true);
        recordingTitleEditView.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    private void InvalidInputValues()
    {
        recordBtn.setEnabled(false);
        recordingTitleEditView.setEnabled(false);
    }

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

        motionTextView = root.findViewById(R.id.motionTextView);
        recordingTitleEditView = root.findViewById(R.id.title_of_recoding_edit_text);

        secondsEditText = root.findViewById(R.id.seconds_text_view);
        secondsEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("rubenbaskaran.com.datarecorderapp", Context.MODE_PRIVATE);
        String length = String.valueOf(sharedPreferences.getInt("length", 0));

        if (dataType.equals(DataTypes.Audio) && Integer.parseInt(length) < 100)
        {
            secondsEditText.setText(length);
        }
        else
        {
            secondsEditText.setText("99");
        }

        intervalEditText = root.findViewById(R.id.interval_text_view);
        intervalEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        repeatsEditText = root.findViewById(R.id.repeats_text_view);
        repeatsEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        recordBtn = root.findViewById(R.id.record_button);
        recordBtn.setOnClickListener(RecordClick);

        stopBtn = root.findViewById(R.id.stop_button);
        stopBtn.setOnClickListener(StopClick);

        ReadyForRecording();

        if (length.equals("0"))
        {
            InvalidInputValues();
        }

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (!secondsEditText.getText().toString().equals(""))
                {
                    newRecordingManager.setCounter(Integer.parseInt(secondsEditText.getText().toString()));

                    if (secondsEditText.getText().toString().equals("0"))
                    {
                        InvalidInputValues();
                    }
                    else if ((dataType.equals(DataTypes.Audio) && secondsEditText.getText().toString().equals("99")) ||
                            (dataType.equals(DataTypes.Motion) && secondsEditText.getText().toString().equals("999")))
                    {
                        ReadyForRecording();
                    }
                    else if (!NewRecordingManager.Recording)
                    {
                        ReadyForRecording();
                    }
                }
                if (!intervalEditText.getText().toString().equals("") && !NewRecordingManager.Recording)
                {
                    newRecordingManager.setInterval(Integer.parseInt(intervalEditText.getText().toString()) * 1000);
                }
                if (!repeatsEditText.getText().toString().equals("") && !NewRecordingManager.Recording)
                {
                    newRecordingManager.setRepeats(Integer.parseInt(repeatsEditText.getText().toString()));
                }
            }
        };

        secondsEditText.addTextChangedListener(textWatcher);
        intervalEditText.addTextChangedListener(textWatcher);
        repeatsEditText.addTextChangedListener(textWatcher);

        newRecordingManager = new NewRecordingManager(intervalEditText, repeatsEditText, recordBtn, stopBtn, secondsEditText, recordingTitleEditView, getContext(), dataType, motionTextView);
        return root;
    }

    View.OnClickListener RecordClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (dataType.equals(DataTypes.Audio))
            {
                NewRecordingManager.Recording = true;
                newRecordingManager.RecordAudio(dataType);
            }
            else if (dataType.equals(DataTypes.Motion))
            {
                NewRecordingManager.Recording = true;
                newRecordingManager.RecordMotion(dataType);
            }
        }
    };

    View.OnClickListener StopClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            NewRecordingManager.Recording = false;
            newRecordingManager.Stop();
        }
    };

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
                secondsEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                if (Integer.parseInt(secondsEditText.getText().toString()) > 99)
                {
                    secondsEditText.setText("99");
                }
            }
            else if (v.getTag().equals("motion"))
            {
                audioButton.setEnabled(true);
                motionButton.setEnabled(false);
                dataType = DataTypes.Motion;
                secondsEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            }
        }
    };
}


