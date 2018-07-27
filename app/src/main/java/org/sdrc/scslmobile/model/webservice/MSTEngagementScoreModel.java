package org.sdrc.scslmobile.model.webservice;

/**
 * Ratikanta Pradhan (ratikanta@sdrc.co.in) on 25-Apr-2017 8:50:50 pm
 */
public class MSTEngagementScoreModel {
	
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
