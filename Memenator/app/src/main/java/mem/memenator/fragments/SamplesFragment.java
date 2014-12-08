package mem.memenator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mem.memenator.R;

/**
 * Fragment for viewing samples from application
 * There is possibility to chose sample and edit it in own way
 */
public class SamplesFragment extends Fragment {
    public SamplesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        View rootView = inflater.inflate(R.layout.friends_fragment, container, false);
        return rootView;
    }
}