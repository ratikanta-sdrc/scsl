package org.sdrc.scslmobile.model.realm;

import io.realm.RealmObject;

/**
 * Created by SDRC_DEV on 09-05-2017.
 */

public class DataVisibility extends RealmObject {
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

    private int deoDeadLine;
    private int subDeadLine;
    private int mneDeadLine;
}
