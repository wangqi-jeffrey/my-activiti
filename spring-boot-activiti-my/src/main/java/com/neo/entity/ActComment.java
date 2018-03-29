package com.neo.entity;

import java.util.Date;

/**
 * activiti批注
 * @author Jeffrey
 * @since 2017年3月29日 下午6:08:54
 */
public class ActComment {
	/**
	 * 批注id
	 */
	private String id;
	/**
	 * 批注时间
	 */
	private Date time;
	/**
	 * 批注人
	 */
	private String userId;
	/**
	 * 批注信息（全）
	 */
	private String fullMessage;
	/**
	 * 任务名称（审批环节）
	 */
	private String taskName;
	/**
	 * 动作（审批结果）
	 */
	private String actionResult;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getActionResult() {
		return actionResult;
	}
	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}
}
