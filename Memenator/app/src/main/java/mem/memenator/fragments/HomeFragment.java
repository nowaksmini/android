package mem.memenator.fragments;

/**
 * Fragment shown after left navigation select home
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mem.memenator.R;
import mem.memenator.adapters.ExpandableNavDrawerListAdapter;
import mem.memenator.adapters.NavDrawerListAdapter;
import mem.memenator.model.NavDrawerItem;

public class HomeFragment extends Fragment {

    private static int EDIT_NUMBER = 3;
    private DrawerLayout mDrawerLayout;  // top-level container for window content
    private ExpandableListView mDrawerList;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private HashMap<NavDrawerItem, List<NavDrawerItem>> navDrawerChildItems;
    private ExpandableNavDrawerListAdapter adapter;
    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        // load slide menu items
        navDrawerChildItems = new HashMap<NavDrawerItem, List<NavDrawerItem>>();
        navMenuTitles = getResources().getStringArray(R.array.right_nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.right_nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.home_drawer_layout);
        mDrawerList = (ExpandableListView) rootView.findViewById(R.id.right_list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Camera
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Gallery
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Samples
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true, "22"));
        // Edit
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setOnGroupClickListener(new SlideMenuClickListener());
        mDrawerList.setOnChildClickListener(new SlideChildClickListener());
        // setting the nav drawer list adapter
        this.prepareListData();
        adapter = new ExpandableNavDrawerListAdapter(rootView.getContext(),
                navDrawerItems, navDrawerChildItems);
        mDrawerList.setAdapter(adapter);
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        return rootView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        String[] navMenuTitlesEdit;
        TypedArray navMenuEditIcons;
        ArrayList<NavDrawerItem> navDrawerEditItems;
        navMenuTitlesEdit = getResources().getStringArray(R.array.right_edit_nav_drawer_items);
        // nav drawer icons from resources
        navMenuEditIcons = getResources().obtainTypedArray(R.array.right_edit_nav_drawer_icons);
        navDrawerEditItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Cut
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[0], navMenuEditIcons.getResourceId(0, -1)));
        // Boarder
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[1], navMenuEditIcons.getResourceId(1, -1)));
        // Solid Brush
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[2], navMenuEditIcons.getResourceId(2, -1)));
        // Text
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[3], navMenuEditIcons.getResourceId(3, -1)));
        // Recycle the typed array
        navMenuEditIcons.recycle();
       // navDrawerEditItems.setOnItemClickListener(new SlideMenuClickListener());
        navDrawerChildItems.put(navDrawerItems.get(EDIT_NUMBER), navDrawerEditItems);
    }

    /**
     * Slide menu group item click listener
     * */
    private class SlideMenuClickListener implements
            ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {
            displayView(position);
            return false;
        }
    }

    /**
     * Slide menu group item click listener
     * */
    private class SlideChildClickListener implements
            ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
            // implement editing
            return false;
        }
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FriendsFragment();
                break;
            case 1:
                fragment = new FindPeopleFragment();
                break;
            case 2:
                fragment = new FriendsFragment();
                break;
            case 3:
                fragment = new FindPeopleFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.home_frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("HomeFragment", "Error in creating fragment");
        }
    }

}