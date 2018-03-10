package rubenbaskaran.com.datarecorderapp.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import rubenbaskaran.com.datarecorderapp.Models.Recording;
import rubenbaskaran.com.datarecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class RecordingAdapter extends ArrayAdapter<Recording>
{
    private Context context;
    private int savedRecordingsRowLayoutId;
    private List<Recording> data;

    public RecordingAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Recording> data)
    {
        super(context, resource, data);

        this.context = context;
        this.savedRecordingsRowLayoutId = resource;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View row = convertView;
        RecordingPlaceHolder pHolder = null;

        if (row == null)
        {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            row = inflater.inflate(savedRecordingsRowLayoutId, parent, false);

            pHolder = new RecordingPlaceHolder();
            pHolder.titleView = (TextView) row.findViewById(R.id.recordingTitleView);

            row.setTag(pHolder);
        }
        else
        {
            pHolder = (RecordingPlaceHolder) row.getTag();
        }

        Recording recordingData = data.get(position);
        pHolder.titleView.setText(recordingData.getTitle());

        return row;
    }

    private static class RecordingPlaceHolder
    {
        TextView titleView;
    }
}