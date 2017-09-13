package rubenbaskaran.com.soundrecorderapp.Fragments;

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

import rubenbaskaran.com.soundrecorderapp.BusinessLogic.NewRecordingManager;
import rubenbaskaran.com.soundrecorderapp.BusinessLogic.RepeatListener;
import rubenbaskaran.com.soundrecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class NewRecordingFragment extends Fragment
{
    private NewRecordingManager newRecordingManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.new_recording_fragment, null);

        TextView secondsTextView = root.findViewById(R.id.seconds_text_view);
        secondsTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        secondsTextView.setText(String.valueOf(0));

        Button incrementBtn = root.findViewById(R.id.increment_button);
        incrementBtn.setOnTouchListener(IncrementClickRepeatListener);

        Button decrementBtn = root.findViewById(R.id.decrement_button);
        decrementBtn.setOnTouchListener(DecrementClickRepeatListener);
        decrementBtn.setEnabled(false);

        Button recordBtn = root.findViewById(R.id.record_button);
        recordBtn.setOnClickListener(RecordClick);
        recordBtn.setEnabled(false);

        EditText recordingTitleEditView = root.findViewById(R.id.title_of_recoding_edit_text);
        recordingTitleEditView.setEnabled(false);

        Button stopBtn = root.findViewById(R.id.stop_button);
        stopBtn.setOnClickListener(StopClick);
        stopBtn.setEnabled(false);

        newRecordingManager = new NewRecordingManager(incrementBtn, decrementBtn, recordBtn, stopBtn, secondsTextView, recordingTitleEditView, getContext());

        return root;
    }

    View.OnClickListener RecordClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            newRecordingManager.Record();
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
}
