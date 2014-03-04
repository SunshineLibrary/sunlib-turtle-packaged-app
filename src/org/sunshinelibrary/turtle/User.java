package org.sunshinelibrary.turtle;

/**
 * Created by hellmagic on 14-2-27.
 */
public class User {
    public String username;
    public String password;
    public String usergroup;
    public Profile profile;

    public User() {
        this.username = "LiuCong";
        this.password = "root";
        this.usergroup = "teacher";
    }

    private class Profile {
        public String gender;
        public String birthday;
    }

    public boolean isProfileFullfill() {
        return ((this.profile.gender != null) && (this.profile.birthday != null));
    }
}

