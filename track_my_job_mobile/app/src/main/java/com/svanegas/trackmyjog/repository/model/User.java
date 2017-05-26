package com.svanegas.trackmyjog.repository.model;

import static com.svanegas.trackmyjog.util.PreferencesManager.ADMIN_ROLE;
import static com.svanegas.trackmyjog.util.PreferencesManager.MANAGER_ROLE;

public class User {

    private long id;
    private String name;
    private String email;
    private String role;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return ADMIN_ROLE.equals(role);
    }

    public boolean isManager() {
        return MANAGER_ROLE.equals(role);
    }
}
