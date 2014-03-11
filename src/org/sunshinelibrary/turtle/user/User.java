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

    public User() {
        this.username = "LiuCong";
        this.password = "root";
        this.usergroup = "teacher";
        this._id = "test";
        this.name = "test";

    }

    private class Profile {
        public String gender;
        public String birthday;
    }

    public boolean isProfileFullfill() {
        return ((this.profile.gender != null) && (this.profile.birthday != null));
    }
}

