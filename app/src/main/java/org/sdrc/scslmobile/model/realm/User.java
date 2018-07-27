package org.sdrc.scslmobile.model.realm;

import io.realm.RealmObject;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta
 */

public class User extends RealmObject{

    private String name;
    private String username;
    private String password;
    private Integer lead;
    private boolean isDEO;
    private String areasIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLead() {
        return lead;
    }

    public void setLead(Integer lead) {
        this.lead = lead;
    }

    public boolean isDEO() {
        return isDEO;
    }

    public void setDEO(boolean DEO) {
        isDEO = DEO;
    }

    public String getAreasIds() {
        return areasIds;
    }

    public void setAreasIds(String areasIds) {
        this.areasIds = areasIds;
    }
}