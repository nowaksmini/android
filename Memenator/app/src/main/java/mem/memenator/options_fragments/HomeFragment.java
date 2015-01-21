package mem.memenator.options_fragments;

/**
 * Fragment shown after left navigation select home
 */


import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mem.memenator.MainActivity;
import mem.memenator.R;
import mem.memenator.adapters.ExpandableNavDrawerListAdapter;
import mem.memenator.model.EditActionType;
import mem.memenator.model.NavDrawerItem;

public class HomeFragment extends Fragment {

    private static int EDIT_NUMBER = 0;
    private DrawerLayout mDrawerLayout;  // top-level container for window content
    private ExpandableListView mDrawerList;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private HashMap<NavDrawerItem, List<NavDrawerItem>> navDrawerChildItems;
    private ExpandableNavDrawerListAdapter adapter;
    private View rootView;
    private String mTitle;
    private String mDrawerTitle;
    private int myIcon;
    private int lastFragment = 0;
    Fragment fragment;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        mDrawerTitle = mTitle = getResources().getStringArray(R.array.right_nav_drawer_items)[0];
        // load slide menu items
        navDrawerChildItems = new HashMap<NavDrawerItem, List<NavDrawerItem>>();
        navMenuTitles = getResources().getStringArray(R.array.right_nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.right_nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.home_drawer_layout);
        mDrawerList = (ExpandableListView) rootView.findViewById(R.id.right_list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Edit
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setOnGroupClickListener(new SlideMenuClickListener());
        mDrawerList.setOnChildClickListener(new SlideChildClickListener());
        // setting the nav drawer list adapter
        this.prepareListData();
        adapter = new ExpandableNavDrawerListAdapter(rootView.getContext(),
                navDrawerItems, navDrawerChildItems);
        mDrawerList.setAdapter(adapter);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        this.changeActionBarTitleAndIcon();
        displayView(lastFragment);
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
        // Solid Brush
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[0], navMenuEditIcons.getResourceId(0, -1)));
        // Text
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[1], navMenuEditIcons.getResourceId(1, -1)));
        // Cut
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[2], navMenuEditIcons.getResourceId(2, -1)));
        // Boarder
        navDrawerEditItems.add(new NavDrawerItem(navMenuTitlesEdit[3], navMenuEditIcons.getResourceId(3, -1)));
        // Recycle the typed array
        navMenuEditIcons.recycle();
       // navDrawerEditItems.setOnItemClickListener(new SlideMenuClickListener());
        navDrawerChildItems.put(navDrawerItems.get(EDIT_NUMBER), navDrawerEditItems);
    }

    private void changeActionBarTitleAndIcon()
    {
        // enabling action bar app icon and behaving it as toggle button
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setHomeButtonEnabled(true);
        // for changing action bar -> image, text etc
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                MainActivity.mDriverIcon = myIcon;
                getActivity().getActionBar().setIcon(myIcon);
                getActivity().getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActivity().getActionBar().setTitle(mTitle);
//                getActivity().getActionBar().setIcon(R.drawable.ic_home);
                // calling onPrepareOptionsMenu() to hide action bar icons
                getActivity().invalidateOptionsMenu();
            }
        };
    }

    /**
     * Slide menu group item click listener
     * */
    private class SlideMenuClickListener implements
            ExpandableListView.OnGroupClickListener {

        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long id) {
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
        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
            // implement
            EditorFragment.Action = EditActionType.values()[childPosition];
            mDrawerList.setSelectedChild(groupPosition, childPosition, true);
            int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
            expandableListView.setItemChecked(index, true);
            mDrawerLayout.closeDrawer(mDrawerList);
            ((EditorFragment)fragment).refreshEditOption();
            return false;
        }
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    public void displayView(int position) {
        // update the main content by replacing fragments
        lastFragment = position;
        fragment = null;
        switch (position) {
            case 0:
                fragment = new EditorFragment();
                myIcon = R.drawable.ic_edit;
                mDrawerTitle = getResources().getStringArray(R.array.right_nav_drawer_items)[0];
                break;
            case 1:
                fragment = new GalleryFragment(true);
                myIcon = R.drawable.ic_gallery;
                mDrawerTitle = getResources().getStringArray(R.array.right_nav_drawer_items)[1];
                // choose photos, create Editor Fragment and show chosen image
                break;
            case 2:
                fragment = new SamplesFragment();
                myIcon = R.drawable.ic_sample;
                mDrawerTitle = getResources().getStringArray(R.array.right_nav_drawer_items)[2];
                break;
            case 3:
                fragment = new PhotoFragment();
                myIcon = R.drawable.ic_camera;
                mDrawerTitle = getResources().getStringArray(R.array.right_nav_drawer_items)[3];
                break;
            default:
                break;
        }

        if (fragment != null) {
            MainActivity.mDriverIcon = myIcon;
            getActivity().getActionBar().setIcon(myIcon);
            getActivity().getActionBar().setTitle(mDrawerTitle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.home_frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            if(position != EDIT_NUMBER) mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("HomeFragment", "Error in creating fragment");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}