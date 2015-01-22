package mem.memenator;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mem.memenator.adapters.TabsPagerAdapter;
import mem.memenator.fonts.FontPickerDialog;
import mem.memenator.logging.LoggingActivity;
import mem.memenator.options_fragments.EditorFragment;
import mem.memenator.options_fragments.SendByBluetoothFragment;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 *
 */
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener, FontPickerDialog.FontPickerDialogListener {
    public static String pictureToEditPath;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    public static Bitmap editedPicture;
    // nav drawer title
    public static int mDriverIcon;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] tabTitles;
    private UiLifecycleHelper uiHelper;
    public static final List<String> PERMISSIONS = Arrays.asList("public_profile");
    public static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    public static boolean pendingPublishReauthorization = false;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = getTitle();
        mDriverIcon = R.drawable.ic_rainbow;
        // load slide menu items
        tabTitles = getResources().getStringArray(R.array.tabs_items);
        this.changeActionBarTitleAndIcon();
        ProfilePictureView profilePictureView = (ProfilePictureView) this.findViewById
                (R.id.selection_profile_pic);

        SharedPreferences settings = getSharedPreferences
                (getResources().getString(R.string.shared_preferences_file), 0);
        profilePictureView.setCropped(true);
        profilePictureView.setProfileId(settings.getString(getResources().getString(R.string.user_id), ""));

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

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        // tabs should stay under action bar
        forceTabs();
        // Adding Tabs
        for (String tab_name : tabTitles) {
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
                mTitle = tabTitles[position];
                mDriverIcon = getResources()
                        .obtainTypedArray(R.array.tab_icons).getResourceId(position, R.drawable.ic_rainbow);
                updateActionBar();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingPublishReauthorization =
                    savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        forceTabs(); // Handle orientation changes.
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
        mTitle = tabTitles[tab.getPosition()];
        mDriverIcon = getResources().obtainTypedArray(R.array.tab_icons).getResourceId
                (tab.getPosition(), R.drawable.ic_rainbow);
        updateActionBar();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onFontSelected(FontPickerDialog dialog) {
        Toast.makeText(this.getBaseContext(), dialog.getSelectedFont(), Toast.LENGTH_LONG).show();
        if (dialog.getSelectedFont().contains("Bold") && dialog.getSelectedFont().contains("Italic"))
            EditorFragment.FONT_ADDITIONAL_STYLE = Typeface.BOLD_ITALIC;
        else if (dialog.getSelectedFont().contains("Bold"))
            EditorFragment.FONT_ADDITIONAL_STYLE = Typeface.BOLD;
        else if (dialog.getSelectedFont().contains("Italic"))
            EditorFragment.FONT_ADDITIONAL_STYLE = Typeface.ITALIC;
        else EditorFragment.FONT_ADDITIONAL_STYLE = Typeface.NORMAL;
        EditorFragment.FONT_STYLE = dialog.getSelectedFont().replaceAll("Bold", "")
                .replaceAll("Italic", "").replaceAll("Normal", "").replaceAll("  ", " ").trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = (String) item.getTitle();
        if (title.equals(getResources().getString(R.string.log_out))) {
            callFacebookLogout(this);
            return true;
        }
        // Handle action bar actions click
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences settings = getSharedPreferences
                (getResources().getString(R.string.shared_preferences_file), 0);
        menu.findItem(R.id.user_name).setTitle
                (settings.getString(getResources().getString(R.string.user_name), ""));
        ProfilePictureView profilePictureView = (ProfilePictureView) this.findViewById
                (R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        profilePictureView.setProfileId(settings.getString(getResources().getString(R.string.user_id), ""));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        updateActionBar();
    }

    private void updateActionBar() {
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
    }

    /**
     * facebook controlling
     */

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            if (pendingPublishReauthorization &&
                    state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
                publishStory(null);
            }
        }
    }

    private void changeActionBarTitleAndIcon() {
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(false);
        // for changing action bar -> image, text etc
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.action_bar, null);
        actionBar.setCustomView(v);
    }

    public void changeColorClick(View v) {
        colorPicker();
    }

    public void changeFontClick(View v) {
        FontPickerDialog dlg = new FontPickerDialog();
        dlg.show(getFragmentManager(), "font_picker");
    }

    public void colorPicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, EditorFragment.COLOR, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            // Executes, when user click Cancel button
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                dialog.getDialog().cancel();
            }

            // Executes, when user click OK button
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                EditorFragment.changeColor(color);
            }
        });
        dialog.show();
    }

    public void saveImageOnDisc(View v) {
        if (editedPicture == null) return;
        FileOutputStream out = null;
        try {
            SharedPreferences settings = getSharedPreferences(getResources().getString
                    (R.string.shared_preferences_file), 0);
            String savePath = settings.getString(getResources().getString(R.string.path_key), "");
            File dir = Environment.getExternalStoragePublicDirectory(savePath);
            dir.mkdirs();
            dir.setWritable(true);
            File file = new File(dir, String.format("MemenatorMyMeme.png", System.currentTimeMillis()));
            file.setWritable(true);
            file.createNewFile();
            out = new FileOutputStream(file);
            editedPicture.compress(Bitmap.CompressFormat.PNG, 85, out); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            out.flush();
            out.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                    file.getName(), file.getName());
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.picture_saved),
                    Toast.LENGTH_LONG).show();
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

    private void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {
            session = new Session(context);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
            //clear your preferences if saved
        }
        Intent intent = new Intent(this, LoggingActivity.class);
        startActivity(intent);
        finish();
    }

    private void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                    .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, false);
        } catch (final Exception e) {
            Log.e("Cannot force action bar tabs to stay under action bar", e.getMessage());
        }
    }

    public void publishStory(View view) {
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
            if (editedPicture != null) {
                Bundle postParams = new Bundle();
                postParams.putString("message", ((TextView) this.findViewById
                        (R.id.publishText)).getText().toString());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                editedPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                postParams.putByteArray("picture", byteArray);


                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Toast.makeText(getApplicationContext(), error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Your post is on facebook", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                Request request = new Request(session, "me/photos", postParams,
                        HttpMethod.POST, callback);
                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
            }
        }
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public void ChangePicture(Bitmap B) {
        editedPicture = B;
        SendByBluetoothFragment.imgAdapt.addBitmap(B);
    }
}

