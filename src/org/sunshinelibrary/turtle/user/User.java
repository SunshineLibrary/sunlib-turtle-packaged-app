package org.sunshinelibrary.turtle.user;

/**
 * Created by hellmagic on 14-2-27.
 */
public class User {
    public String username;
    public String password;
    public String usergroup;
    public Profile profile;
    public String _id;
    public String name;

    public User(){
        this.username = "test";
        this.usergroup = "test";
        this.name = "test";
        this._id = "test";
    }

    private class Profile {
        public String gender;
        public String birthday;
    }

    public boolean isProfileFullfill() {
        return ((this.profile.gender != null) && (this.profile.birthday != null));
    }
}

