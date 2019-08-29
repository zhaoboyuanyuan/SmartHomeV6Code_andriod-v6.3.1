package cc.wulian.smarthomev6.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RulesGroupInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String groupID;
	private String status;
	private List<String> programIDList = new ArrayList<String>();
	
	public RulesGroupInfo() {
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getProgramIDList() {
		return programIDList;
	}

	public void setProgramIDList(List<String> programIDList) {
		this.programIDList = programIDList;
	}
	
	
}
