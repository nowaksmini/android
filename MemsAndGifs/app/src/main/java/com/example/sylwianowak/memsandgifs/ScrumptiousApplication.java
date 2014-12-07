package com.example.sylwianowak.memsandgifs;

import android.app.Application;

import com.facebook.model.GraphUser;

import java.util.List;

/**
 * Created by Sylwia.Nowak on 2014-11-17.
 */
public class ScrumptiousApplication extends Application {
    private List<GraphUser> selectedUsers;
    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> users) {
        selectedUsers = users;
    }
}
