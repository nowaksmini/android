package mem.memenator;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.widget.ProfilePictureView;

import java.io.File;
import java.util.ArrayList;

import mem.memenator.adapters.NavDrawerListAdapter;
import mem.memenator.adapters.TabsPagerAdapter;
import mem.memenator.model.NavDrawerItem;
import mem.memenator.options_fragments.FindPeopleFragment;
import mem.memenator.options_fragments.GalleryFragment;
import mem.memenator.options_fragments.HomeFragment;

/**
 *
 */
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;  // top-level container for window content
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Bitmap editedPicture;
    public static String pictureToEditPath;
    // nav drawer title
    private CharSequence mDrawerTitle;
    public static int mDriverIcon;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mTitle = mDrawerTitle = getTitle();
        mDriverIcon = R.drawable.ic_rainbow;
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.left_nav_drawer_items);
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.left_nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_list_slider_menu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Gallery
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Find
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Friends
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        this.changeActionBarTitleAndIcon();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ProfilePictureView profilePictureView = (ProfilePictureView) this.findViewById
                (R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        SharedPreferences settings = getSharedPreferences
                (getResources().getString(R.string.shared_preferences_file), 0);
        profilePictureView.setProfileId(settings.getString(getResources().getString(R.string.user_id),""));
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        String savePath = settings.getString(getResources().getString(R.string.path_key), "");
        // "" means default value if didn't found key
        settings.edit().remove(getResources().getString(R.string.path_key)).commit();
        savePath = "";
        if (savePath.isEmpty()) {
            File file = new File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath(),
                    String.valueOf(getResources().getText(R.string.album_name).toString()));
            settings.edit().putString(getResources().getString
                    (R.string.path_key), file.getAbsolutePath()).commit();
        }
        this.displayView(0);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : navMenuTitles) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

        @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // on tab selected
            // show respected fragment view
            viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * Displaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                mDriverIcon = R.drawable.ic_home;
                break;
            case 1:
                fragment = new GalleryFragment(false);
                mDriverIcon = R.drawable.ic_photo;
                break;
            case 2:
                fragment = new FindPeopleFragment();
                mDriverIcon = R.drawable.ic_search;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void changeActionBarTitleAndIcon() {
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // for changing action bar -> image, text etc
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                getActionBar().setIcon(mDriverIcon);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setIcon(R.drawable.ic_rainbow);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.action_bar, null);
        actionBar.setCustomView(v);
    }
}

