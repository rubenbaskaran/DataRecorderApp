package rubenbaskaran.com.datarecorderapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import rubenbaskaran.com.datarecorderapp.Adapters.RecordingAdapter;
import rubenbaskaran.com.datarecorderapp.BusinessLogic.SavedRecordingsManager;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class SavedRecordingsFragment extends Fragment
{
    private RecordingAdapter recordingAdapter;
    private SavedRecordingsManager savedRecordingsManager;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.saved_recordings_fragment, null);

        ListView savedRecordingsListView = root.findViewById(R.id.savedRecordingsListView);
        savedRecordingsListView.setOnItemClickListener(EditOnItemClick);
        savedRecordingsManager = new SavedRecordingsManager(getContext());
        recordingAdapter = new RecordingAdapter(getActivity().getApplicationContext(), R.layout.saved_recordings_row, savedRecordingsManager.getRecordingData());
        savedRecordingsListView.setAdapter(recordingAdapter);

        return root;
    }

    AdapterView.OnItemClickListener EditOnItemClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
        {
            savedRecordingsManager.OpenEditRecordingFragment(position, getFragmentManager().beginTransaction());
        }
    };
}
