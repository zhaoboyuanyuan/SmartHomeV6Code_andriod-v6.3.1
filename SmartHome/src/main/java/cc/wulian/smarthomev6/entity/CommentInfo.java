package cc.wulian.smarthomev6.entity;

import java.io.Serializable;

public class CommentInfo implements Serializable
{
	public static final String GW_COMMENT_GWID = "gwID";
	public static final String GW_COMMENT_USERTOKEN = "userToken";
	public static final String GW_COMMENT_APPID = "appID";
	public static final String GW_COMMENT_HARDWARELEVEL = "hardwareLevel";
	public static final String GW_COMMENT_SOFTLEVEL = "softLevel";
	public static final String GW_COMMENT_SALESERVICELEVEL = "saleServiceLevel";
	public static final String GW_COMMENT_COMMENTS = "comments";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appID;
	private String gwID;
	private String time;
	private String comment;

	public CommentInfo() {

	}
	
	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
