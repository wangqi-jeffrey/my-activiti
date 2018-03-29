package com.neo.entity;

import java.util.Date;

/**
 * activiti任务实体
 * @author Jeffrey
 * @since 2017年3月29日 下午5:01:49
 */
public class ActTask {
	/**
	 * 任务ID
	 */
	private String id;
	/**
	 * 任务名称
	 */
	private String name;
	/**
	 * 任务的创建时间
	 */
	private Date createTime;
	/**
	 * 任务的办理人
	 */
	private String assignee;
	/**
	 * 流程实例ID
	 */
	private String processInstanceId;
	/**
	 * 执行对象ID
	 */
	private String executionId;
	/**
	 * 流程定义ID
	 */
	private String ProcessDefinitionId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getProcessDefinitionId() {
		return ProcessDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		ProcessDefinitionId = processDefinitionId;
	}
}
