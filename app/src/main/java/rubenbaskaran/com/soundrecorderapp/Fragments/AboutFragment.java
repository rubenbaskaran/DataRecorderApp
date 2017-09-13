package rubenbaskaran.com.soundrecorderapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rubenbaskaran.com.soundrecorderapp.R;

/**
 * Created by Ruben on 14-07-2017.
 */

public class AboutFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.about_fragment, null);
        return root;
    }
}
