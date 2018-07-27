package org.sdrc.scslmobile.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta
 */

public class TypeDetail extends RealmObject {

    @PrimaryKey
    private int typeDetailId;
    private String typeDetail;
    private String description;
    private int typeId;

    public int getTypeDetailId() {
        return typeDetailId;
    }

    public void setTypeDetailId(int typeDetailId) {
        this.typeDetailId = typeDetailId;
    }

    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
