package com.anxpp.demo.activiti.simple.core.service;

import java.util.List;

import org.activiti.engine.task.Task;

import com.anxpp.demo.activiti.simple.core.entity.ApplySimple;

public interface ApplySimpleService {
	int STATE_START = 1;
	int STATE_PASS = 2;
	int STATE_NO_PASS = 0;
	/**
	 * 提交申请（启动流程）
	 * @param applySimple
	 */
	void startProcess(ApplySimple applySimple);
	/**
	 * 获取用户任务数
	 * @param uid
	 */
	Long countTask(Long uid);
	/**
	 * 获取用户任务
	 * @return
	 */
	List<Task> getTaskByUid(Long uid);
	/**
	 * 完成审核
	 * @param checkFlag
	 */
	void completeSimpleCheck(String taskId,Integer checkFlag);
	/**
	 * 更新申请单业务状态
	 * @param id
	 * @param state
	 */
	void updateSimpleState(Long id,Integer state);
	/**
	 * 获取历史流程总数
	 * @return
	 */
	Long countProcess();
}
