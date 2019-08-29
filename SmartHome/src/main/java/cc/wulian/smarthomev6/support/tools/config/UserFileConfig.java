package cc.wulian.smarthomev6.support.tools.config;

import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;


public class UserFileConfig {

	public static final String HEAD_ICON = "temp_head.png";
	private static UserFileConfig instance;
	private long timeout = DateUtil.MILLI_SECONDS_OF_DAY;
	private String userID = "0000";
	private String folder = "";

	private UserFileConfig() {
		this.updateFold();
	}

	private void updateFold() {
		folder = FileUtil.getUserDirectoryPath() + "/" + this.userID;
		FileUtil.isFolderExists(folder);
	}

	public void setUserID(String userID) {
		this.userID = userID;
		this.updateFold();
	}

	public static UserFileConfig getInstance() {
		if (instance == null) {
			instance = new UserFileConfig();
		}
		return instance;
	}
	
	public final String getUserPath() {
		return folder;
	}
	
	public final String getUserFile(String filename) {
		return folder + "/" + filename;
	}


}
