package cc.wulian.smarthomev6.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.support.utils.StringUtil;


public class AutoProgramTaskInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gwID;
	private String programID;
	private String programName;
	private String programDesc;
	private String status;
	private String programType;
	private List<AutoConditionInfo> triggerList = new ArrayList<AutoConditionInfo>();
	private ConditionNode root = null;
	private List<AutoActionInfo> actionList = new ArrayList<AutoActionInfo>();
	
	public AutoProgramTaskInfo(){
		
	}
	
	public AutoProgramTaskInfo clone() {
		AutoProgramTaskInfo newInfo = new AutoProgramTaskInfo();
		newInfo.gwID = this.gwID;
		newInfo.programID = this.programID;
		newInfo.programName = this.programName;
		newInfo.programDesc = this.programDesc;
		newInfo.status = this.status;
		return newInfo;
	}
	@Override
	public boolean equals( Object obj ){
		if(obj instanceof TaskInfo){
			AutoProgramTaskInfo task = (AutoProgramTaskInfo) obj;
			return 
					StringUtil.equals(gwID, task.gwID)
			 && StringUtil.equals(programID, task.programID) 
			 && StringUtil.equals(programName, task.programName) 
			 && StringUtil.equals(programDesc, task.programDesc) 
			 && StringUtil.equals(status, task.status) 
			 ;
		}
		else {
			return super.equals(obj);
		}
	}
	
	public void addTrigger(AutoConditionInfo newinfo){
		int count =0 ;
		while(count < triggerList.size()){
			AutoConditionInfo triggerInfo = triggerList.get(count);
			if(StringUtil.equals(triggerInfo.getType(),newinfo.getType())
					&& StringUtil.equals(triggerInfo.getObject(),newinfo.getObject())
					&& StringUtil.equals(triggerInfo.getExp(),newinfo.getExp()))
				break;
			else
				count++;
		}
		if(count == triggerList.size())
			triggerList.add(newinfo);
	}
	public void updateTrigger(AutoConditionInfo triggerInfo,AutoConditionInfo newtriggerInfo){
		removeTrigger(triggerInfo.getType(),triggerInfo.getObject(), triggerInfo.getExp());
		addTrigger(newtriggerInfo);
	}
	public void removeTrigger(String type, String object ,String exp){
		triggerList.remove(getTrigger(type, object,exp));
	}
	public AutoConditionInfo getTrigger(String type, String object,String exp){
		for(AutoConditionInfo info : triggerList){
			if(info.getType().equals(type) && info.getObject().equals(object) && info.getExp().equals(exp)){
				return info;
			}
		}
		return null;
	}
	public void triggerListclear(){
		triggerList.clear();
	}

	/**
	 * add Condition to program
	 * @param relative
	 * can be "and"/"or", represent the relation of condition
	 * @param condition
	 * The format is {@code type.object notin|in|>|<|= value }
	 * object取值：
	 * type=0:取值为当前场景CURSCENE
	 * type=1:取值为当前时间CURTIME
	 * type=2:设备的标识组合，{@code devID>devType>ep>epType，设备ID、设备类型、设备端口、设备端口类型按顺序用">"连接起来}
	 * 操作符取值：{@code 有 in、notin、>、<、＝。in和notin主要用在type＝0和type=1时}
	 * type=3：取值为当前用户CURUSER
	 * value取值：
	 * type=0:当前场景是value集合中的某个场景时为true，取值方式是：(场景Aid,场景Bid)
	 * type=1:当属于该时间段时为true，取值方式是:HHMMXXXXAB。前4位表示起始时间；4~8位表示持续时间，整数，单位是分钟，最大为1440；9~10位表示按周重复规律，为16进制数，转成2进制后，从低到高，表示星期一到星期天，为0表示不生效，为1表示生效，例如7F表示每天都生效。
	 * type=2:预设置的值，当传感器当前值满足表达式时为True。
	 * type=3:通过接口传输的当前用户名
	 */
	public void addConditionTree(String relative,String condition){
		if(root == null){
			root = new ConditionNode(condition);
		}else{
			root = root.addCondition(relative, condition);
		}
	}
	
	public void deleteConditionTree(String condition){
		if(root != null){
			root = root.deleteCondition(condition);
		}
	}
	
	public void updateConditionTree(String condition,String newCondition){
		if(root != null){
			root = root.updateCondition(condition, newCondition);
		}
	}
	public ConditionNode getConditionFromTree(String condition){
		if(!StringUtil.isNullOrEmpty(condition)){
			root.search(condition);
		}
		return null;
	}
	
	public void addActionTask(AutoActionInfo actioninfo){
		int count =0 ;
		while(count < actionList.size()){
			//添加设备任务时不用过滤
//			AutoActionInfo taskInfo = actionList.get(count);
//			if(StringUtil.equals(taskInfo.getType(),actioninfo.getType()) &&
//					StringUtil.equals(taskInfo.getObject(),actioninfo.getObject()) &&
//					StringUtil.equals(taskInfo.getEpData(),actioninfo.getEpData()))
//				break;
//			else
			count++;
		}
		if(count == actionList.size())
			actionList.add(actioninfo);
	}
	public void updateActionTask(AutoActionInfo actionInfo){
		removeActionTask(actionInfo.getSortNum());
		addActionTask(actionInfo);
		
	}
	public void removeActionTask(String sortNum){
		actionList.remove(getActionTask(programID, sortNum));
	}
	public AutoActionInfo getActionTask(String programID,String sortNum){
		for(AutoActionInfo info : actionList){
			if(info.getSortNum().equals(sortNum)){
				return info;
			}
		}
		return null;
	}
	public void actionListclear(){
		actionList.clear();
	}

	public String getGwID() {
		return gwID;
	}


	public void setGwID(String gwID) {
		this.gwID = gwID;
	}


	public String getProgramID() {
		return programID;
	}


	public void setProgramID(String programID) {
		this.programID = programID;
	}


	public String getProgramName() {
		return programName;
	}


	public void setProgramName(String programName) {
		this.programName = programName;
	}


	public String getProgramDesc() {
		return programDesc;
	}


	public void setProgramDesc(String programDesc) {
		this.programDesc = programDesc;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public List<AutoConditionInfo> getTriggerList() {
		return triggerList;
	}


	public void setTriggerList(List<AutoConditionInfo> triggerList) {
		this.triggerList = triggerList;
	}


	public ConditionNode getRoot() {
		return root;
	}

	public void setRoot(ConditionNode root) {
		this.root = root;
	}


	public List<AutoActionInfo> getActionList() {
		return actionList;
	}


	public void setActionList(List<AutoActionInfo> actionList) {
		this.actionList = actionList;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
