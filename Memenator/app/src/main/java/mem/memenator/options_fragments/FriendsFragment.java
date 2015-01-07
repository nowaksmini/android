package mem.memenator.options_fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mem.memenator.R;

/**
 * Fragment shown after left navigation select friends
 */
public class FriendsFragment extends Fragment {
    public FriendsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        View rootView = inflater.inflate(R.layout.friends_fragment, container, false);
        return rootView;
    }
}