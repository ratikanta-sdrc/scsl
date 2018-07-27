package org.sdrc.scslmobile.model.realm;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Subhadarshani on 26-04-2017.
 */

public class SysConfig extends RealmObject {

    private Date lastSyncDate;
    private int deoDeadLine;
    private int subDeadLine;
    private int mneDeadLine;

    public Date getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public int getDeoDeadLine() {
        return deoDeadLine;
    }

    public void setDeoDeadLine(int deoDeadLine) {
        this.deoDeadLine = deoDeadLine;
    }

    public int getSubDeadLine() {
        return subDeadLine;
    }

    public void setSubDeadLine(int subDeadLine) {
        this.subDeadLine = subDeadLine;
    }

    public int getMneDeadLine() {
        return mneDeadLine;
    }

    public void setMneDeadLine(int mneDeadLine) {
        this.mneDeadLine = mneDeadLine;
    }
}
