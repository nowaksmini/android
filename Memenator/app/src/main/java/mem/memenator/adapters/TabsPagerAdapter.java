package mem.memenator.adapters;

/**
 * Fragment Adapter for upper tabs home, bluetooth, my Memes
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mem.memenator.options_fragments.EditorFragment;
import mem.memenator.options_fragments.SendByBluetoothFragment;
import mem.memenator.options_fragments.GalleryFragment;
import mem.memenator.options_fragments.PhotoFragment;
import mem.memenator.options_fragments.SamplesFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new EditorFragment();
            case 1:
                return new SendByBluetoothFragment();
            case 2:
                // show only my memes
                return new GalleryFragment(false);
            case 3:
                // whole gallery
                return new GalleryFragment(true);
            case 4:
                return new SamplesFragment();
            case 5:
                return new PhotoFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 6;
    }

}
