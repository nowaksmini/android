package mem.memenator.adapters;

/**
 * Fragment Adapter for upper tabs home, bluetooth, my Memes
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mem.memenator.options_fragments.FindPeopleFragment;
import mem.memenator.options_fragments.GalleryFragment;
import mem.memenator.options_fragments.HomeFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new HomeFragment();
            case 1:
                return new FindPeopleFragment();
            case 2:
                // show only samples
                return new GalleryFragment(false);
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
