package mem.memenator.logging;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mem.memenator.MainActivity;

/**
 * Activity created firstly, Main Activity starts after successfully my_menu
 */
public class LoggingActivity extends FragmentActivity {

    private LoggingFragment loggingFragment;
    private static final int LOGGING_PANEL = 0;
    private static final int MAIN_PANEL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loggingFragment = new LoggingFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loggingFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            loggingFragment = (LoggingFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the main activity
            showView(MAIN_PANEL);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showView(LOGGING_PANEL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Only for debugging purpose, problems with key_hash
     */
    private void checkKeyHash() {
        //something wrong with key hash - solved
        try {
            PackageInfo info = getPackageManager().getPackageInfo("mem.memenator",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void showView(int index) {
        if (index == MAIN_PANEL) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (index == LOGGING_PANEL) {
            loggingFragment = new LoggingFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loggingFragment)
                    .commit();
        }
    }
}
