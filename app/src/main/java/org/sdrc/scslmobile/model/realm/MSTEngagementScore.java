package org.sdrc.scslmobile.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Subhadarshani on 24-04-2017.
 * Ratikanta pradhan
 */

public class MSTEngagementScore extends RealmObject {
    @PrimaryKey
    private int mstEngagementScoreId;
    private String progress;
    private String definition;
    private float score;

    public int getMstEngagementScoreId() {
        return mstEngagementScoreId;
    }

    public void setMstEngagementScoreId(int mstEngagementScoreId) {
        this.mstEngagementScoreId = mstEngagementScoreId;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
