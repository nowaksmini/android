package mem.memenator.fragments;

/**
 * Created by Sylwia.Nowak on 2014-12-07.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mem.memenator.R;

/**
 * Fragment shown after left navigation select find people
 */
public class FindPeopleFragment extends Fragment {
    public FindPeopleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        View rootView = inflater.inflate(R.layout.find_people_fragment, container, false);
        return rootView;
    }
}