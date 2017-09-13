package rubenbaskaran.com.soundrecorderapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rubenbaskaran.com.soundrecorderapp.BusinessLogic.EditRecordingManager;
import rubenbaskaran.com.soundrecorderapp.R;

/**
 * Created by Ruben on 19-07-2017.
 */

public class EditRecordingFragment extends Fragment
{
    private Button editTitleBtn;
    private TextView title;
    private String Title, Timestamp, Filepath;
    private EditRecordingManager editRecordingManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.edit_recording_fragment, null);

        String Length;
        TextView length, timestamp;

        Bundle extras = getArguments();
        Title = extras.getString("title");
        Length = extras.getString("length");
        Timestamp = extras.getString("timestamp");
        Filepath = extras.getString("filepath");
        int position = extras.getInt("position");

        editRecordingManager = new EditRecordingManager(position);

        title = root.findViewById(R.id.title_text_view_edit_recording_fragment);
        title.setText(Title);
        title.setEnabled(false);

        length = root.findViewById(R.id.length_text_view_edit_recording_fragment);
        length.setText(Length);

        timestamp = root.findViewById(R.id.timestamp_text_view_edit_recording_fragment);
        timestamp.setText(Timestamp);

        editTitleBtn = root.findViewById(R.id.edit_title_button);
        editTitleBtn.setOnClickListener(EditTitleClicked);

        Button deleteRecordingBtn = root.findViewById(R.id.delete_recording_button);
        deleteRecordingBtn.setOnClickListener(DeleteRecordingClicked);

        return root;
    }

    View.OnClickListener EditTitleClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            editRecordingManager.EditTitle(title, Timestamp, editTitleBtn, getContext());
        }
    };

    View.OnClickListener DeleteRecordingClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            editRecordingManager.DeleteRecording(getContext(), getFragmentManager().beginTransaction(), Title, Filepath, Timestamp);
        }
    };
}
