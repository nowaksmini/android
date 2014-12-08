package mem.memenator.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import mem.memenator.R;

/**
 * Fragment for editing images, publishing them, opens after selecting
 * camera and taking picture, choosing image to edit (gallery, samples)
 * or just choosing option edit, everything from right navigation drawer
 */
public class EditorFragment extends Fragment {
    public  EditorFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        View rootView = inflater.inflate(R.layout.editor_fragment, container, false);
        return rootView;
    }
}