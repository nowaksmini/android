package mem.memenator.logging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import mem.memenator.R;

/**
 * Main my_menu fragment for facebook, my_menu button, etc.
 */
public class LoggingFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private static final String TAG = "LoggingFragment";
    // for listening for session status changing logic
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.facebook_logging_fragment, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.login_button);
        authButton.setFragment(this);
        return view;
    }


    /**
     * UiLifecycleHelper object creates the Facebook session and opens it automatically
     * if a cached token is available.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /**
     * must override for better controlling of fragment to ensure that the sessions are set up correctly,
     */

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&(session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                SharedPreferences settings = view.getContext().getSharedPreferences
                                        (view.getContext().getResources().getString(R.string.shared_preferences_file), 0);
                                String user_name = settings.getString(view.getContext().getResources().getString(R.string.user_name), "");
                                settings.edit().remove(view.getContext().getResources().getString(R.string.user_name))
                                        .commit();
                                user_name = user.getName();
                                settings.edit().putString(view.getContext().getResources().getString
                                        (R.string.user_name), user_name).commit();
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                String user_id = user.getId();
                                settings.edit().remove(view.getContext().getResources().getString(R.string.user_id))
                                        .commit();
                                settings.edit().putString(view.getContext().getResources().getString
                                        (R.string.user_id), user_id).commit();
                            }
                        }
                    }
                });
        request.executeAsync();
    }
}
