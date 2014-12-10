package mem.memenator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;

import mem.memenator.adapters.NavDrawerListAdapter;
import mem.memenator.colors.ColorPickerDialog;
import mem.memenator.fragments.EditorFragment;
import mem.memenator.fragments.FindPeopleFragment;
import mem.memenator.fragments.FriendsFragment;
import mem.memenator.fragments.GalleryFragment;
import mem.memenator.fragments.HomeFragment;
import mem.memenator.model.NavDrawerItem;

/**
 * Creating a NavDrawerListAdapter instance and adding list items.
 > Assigning the adapter to Navigation Drawer ListView
 > Creating click event listener for list items
 > Creating and displaying fragment activities on selecting list item.
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {
    TextView testView;
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
    public MainActivity()
    { }

    public void saveImageOnDisc(View v) {
        if(editedPicture == null) return;
        FileOutputStream out = null;
        try {
            SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_file), 0);
            String savePath = settings.getString(getResources().getString(R.string.path_key),"");

            File dir = Environment.getExternalStoragePublicDirectory(savePath);
            //File dir = new File(savePath);
            dir.mkdirs();
            dir.setWritable(true);
            File file = new File(dir,String.format("MemenatorMyMeme.png", System.currentTimeMillis()));
            file.setWritable(true);
            String s = file.getPath();
            //  File Temp = file.getParentFile();
            file.createNewFile();
            out = new FileOutputStream(file);
            editedPicture.compress(Bitmap.CompressFormat.PNG, 85, out); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            out.flush();
            out.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            //editedPicture.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.picture_saved), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.problem_saving_photo), Toast.LENGTH_LONG).show();
            }
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
        mDrawerList = (ListView) findViewById(R.id.left_list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Gallery
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Find
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true, "22"));
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

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_file), 0);
        String savePath = settings.getString(getResources().getString(R.string.path_key),""); // "" means default value if didn't found key
        settings.edit().remove(getResources().getString(R.string.path_key)).commit();
        savePath = "";
        if(savePath.isEmpty())
        {
            File file = new File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath(), String.valueOf(getResources().getText(R.string.album_name).toString()));
            settings.edit().putString(getResources().getString(R.string.path_key),file.getAbsolutePath()).commit();
        }
        this.displayView(0);
    }

    private void changeActionBarTitleAndIcon()
    {
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
    }

    @Override
    public void colorChanged(int color) {
        EditorFragment.changeColor(color);
    }

    public void changeColorClick(View v)
    {
        new ColorPickerDialog(this,this, EditorFragment.COLOR).show();
    }
    /**
     * Slide menu item click listener
     * */
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
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
            case 3:
                fragment = new FriendsFragment();
                mDriverIcon = R.drawable.ic_friends;
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

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        getActionBar().setIcon(mDriverIcon);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}