package com.anxpp.demo.activiti.simple.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anxpp.demo.activiti.core.repo.UserRepo;
import com.anxpp.demo.activiti.simple.core.entity.ApplySimple;
import com.anxpp.demo.activiti.simple.core.repo.ApplySimpleRepo;
import com.anxpp.demo.activiti.simple.core.service.ApplySimpleService;

@Service
public class ApplySimpleImpl implements ApplySimpleService {
	private final static Logger log = LoggerFactory.getLogger(ApplySimpleImpl.class);
	
	private final static String PROCESS_NAME = "simpleProcess";

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ApplySimpleRepo applySimpleRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private HistoryService historyService;

	@Override
	@Transactional	//此处不含spring boot预设的切面中包含事务的字段，需要手动设置
	public void startProcess(ApplySimple applySimple) {
		log.info("开始申请...");
		applySimple.setState(STATE_START);
		//保存业务数据
		applySimpleRepo.save(applySimple);
		//启动流程
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", applySimple.getInsertBy());
		Long dept = userRepo.findDeptById(applySimple.getInsertBy());
		log.info(dept.toString());
		params.put("dept", dept);
		runtimeService.startProcessInstanceByKey(PROCESS_NAME, params);
	}

	@Override
	public Long countTask(Long uid) {
		return taskService.createTaskQuery()
			.processDefinitionKey(PROCESS_NAME)
			.taskCandidateUser(uid.toString())
			.count();
	}

	@Override
	public List<Task> getTaskByUid(Long uid) {
		return taskService.createTaskQuery()
				.processDefinitionKey(PROCESS_NAME)
				.taskCandidateUser(uid.toString())
				.list();
	}
	
	@Override
	@Transactional
	public void completeSimpleCheck(String taskId,Integer checkFlag) {
		log.info("完成任务："+taskId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("checkFlag", checkFlag);
		taskService.complete(taskId, params);
	}

	@Override
	public void updateSimpleState(Long id, Integer state) {
		applySimpleRepo.updateState(id, state);
	}

	@Override
	public Long countProcess() {
		return historyService.createHistoricProcessInstanceQuery()
					.processDefinitionKey(PROCESS_NAME)
					.count();
	}

}