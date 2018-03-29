package com.neo.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neo.entity.ActComment;
import com.neo.entity.ActTask;

@Service
public class WorkflowService {
	private final Logger logger = LoggerFactory.getLogger(WorkflowService.class);
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RuntimeService runtimeService;

	/**
	 * 查询部署列表
	 * @author Jeffrey
	 * @since 2017年3月29日 下午6:00:16
	 * @return
	 */
	public Object findDeployments() {
		// 1:查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> depList = repositoryService.createDeploymentQuery() // 创建部署对象查询
				.orderByDeploymenTime().asc().list();
		
		// 2:查询流程定义的信息，对应表（act_re_procdef）
		List<ProcessDefinition> pdList = repositoryService
				.createProcessDefinitionQuery() // 创建流程定义查询
				.orderByProcessDefinitionVersion().asc().list();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("depList", depList);
		map.put("pdList", pdList);
		// System.out.println(depList.size() + "----" + pdList.size());
		for (Deployment deployment : depList) {
			System.out.println(deployment);
			System.err.println(deployment.getName());
		}
		return null;
	}

	/**
	 * 查询流程定义的流程图
	 * @author Jeffrey
	 * @since 2017年3月29日 下午5:59:54
	 * @param deploymentId
	 * @param imageName
	 * @return
	 */
	public InputStream findImageInputStream(String deploymentId,
			String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	/**
	 * 根据办理人查询待办任务
	 * @author Jeffrey
	 * @since 2017年3月29日 下午5:59:39
	 * @param userName 用户名（唯一）
	 * @return
	 */
	public List<ActTask> findTaskListByName(String userName) {
		List<Task> list = taskService.createTaskQuery()//
				.taskAssignee(userName) // 指定个人任务查询
				.orderByTaskCreateTime().asc() // 按任务创建时间升序排序
				.list();
		if (list == null || list.size() == 0) return null;
		List<ActTask> actTasks = new ArrayList<ActTask>();
		for (Task task : list){
			ActTask actTask = new ActTask();
			actTask.setId(task.getId());
			actTask.setName(task.getName());
			actTask.setCreateTime(task.getCreateTime());
			actTask.setAssignee(task.getAssignee());
			actTask.setProcessInstanceId(task.getProcessInstanceId());
			actTask.setExecutionId(task.getExecutionId());
			actTask.setProcessDefinitionId(task.getProcessDefinitionId());
			actTasks.add(actTask);
		}
		return actTasks;
	}
	
	/**
	 * 根据对应的标识查询历史的批注信息
	 * @author Jeffrey
	 * @since 2017年3月29日 下午6:02:03
	 * @param busiId 业务表单id
	 * @param classSimpleName 对应业务类名
	 * @return
	 */
	public List<ActComment> findCommentByTag(Integer busiId, String classSimpleName) {
		// 组织流程表中的字段中的值
		String objId = classSimpleName + "." + busiId;
		
		/**1:使用历史的流程实例查询，返回历史的流程实例对象，获取流程实例ID*/
		HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery() // 对应历史的流程实例表
						.processInstanceBusinessKey(objId) // 使用BusinessKey字段查询
						.singleResult();
		// 流程实例ID
		String processInstanceId = hpi.getId();
		List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
		if (list == null || list.size() == 0) return null;
		List<HistoricVariableInstance> varList = historyService.createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询对象
									.processInstanceId(processInstanceId).list();
		List<ActComment> actComments = new ArrayList<ActComment>();
		for (Comment comment : list) {
			ActComment actComment = new ActComment();
			actComment.setId(comment.getId());
			actComment.setTime(comment.getTime());
			actComment.setUserId(comment.getUserId());
			actComment.setFullMessage(comment.getFullMessage());
			actComment.setTaskName((String) getVariable(varList, comment.getTaskId()).get("taskName"));
			actComment.setActionResult((String) getVariable(varList, comment.getTaskId()).get("actionResult"));
			actComments.add(actComment);
		}
		return actComments;
	}
	
	private Map<String, Object> getVariable(List<HistoricVariableInstance> list, String taskId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(list == null || list.size() == 0) return map;
		for(HistoricVariableInstance hvi:list){
			if (taskId.equals(hvi.getTaskId())) {
				map.put(hvi.getVariableName(), hvi.getValue());
			}
		}
		return map;
	}

	/**
	 * 获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
	 * @author Jeffrey
	 * @since 2017年3月29日 下午6:48:22
	 * @param taskId
	 * @return
	 */
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()
					.taskId(taskId) // 使用任务ID查询
					.singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery() // 创建流程定义查询对象，对应表act_re_procdef 
					.processDefinitionId(processDefinitionId) // 使用流程定义ID查询
					.singleResult();
		return pd;
	}

	/**
	 * 查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
		 map集合的key：表示坐标x,y,width,height
		 map集合的value：表示坐标对应的值
	 * @author Jeffrey
	 * @since 2017年3月29日 下午7:04:52
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> findCoordingByTask(String taskId) {
		// 存放坐标
		Map<String, Object> map = new HashMap<String, Object>();
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()// 创建流程实例查询
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的ID
		String activityId = pi.getActivityId();
		// 获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity
				.findActivity(activityId);// 活动ID
		// 获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}
	
	/**
	 * 使用任务ID，查找业务ID，从而获取业务信息
	 * @author Jeffrey
	 * @since 2017年3月30日 上午11:12:13
	 * @param taskId
	 * @return
	 */
	public String findBuninessByTaskId(String taskId) {
		// 1：使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		// 5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）
		String id = null;
		if (StringUtils.isNotBlank(buniness_key)) {
			// 截取字符串，取buniness_key小数点的第2个值
			id = buniness_key.split("\\.")[1];
		}
		return id;
	}
	
	/**
	 * 已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 * @author Jeffrey
	 * @since 2017年3月30日 上午11:19:13
	 * @param taskId
	 * @return
	 */
	public List<String> findOutComeListByTaskId(String taskId) {
		// 1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();

		// 获取当前活动的id
		String activityId = pi.getActivityId();
		// 4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity
				.findActivity(activityId);
		return nextTaskLineNames(activityImpl);
	}
	
	/**
	 * 获取下一个当前任务之后的连线名称
	 * @author Jeffrey
	 * @since 2017年3月28日 下午4:44:18
	 * @return
	 */
	private List<String> nextTaskLineNames(ActivityImpl activityImpl) {
		List<String> list = new ArrayList<String>();

		List<PvmTransition> outTransitions = activityImpl
				.getOutgoingTransitions();
		List<PvmTransition> outTransitionsTemp = null;
		// 当前任务之后没有连线
		if (outTransitions == null || outTransitions.size() == 0)
			return null;

		// 只有一条连线并且名称为空
		if (outTransitions.size() == 1) {
			PvmTransition pvm = outTransitions.get(0);
			PvmActivity pvmAc = pvm.getDestination();
			if ("userTask".equals(pvmAc.getProperty("type"))
					&& (pvm.getProperty("name") == null || ""
							.equals(((String) pvm.getProperty("name")).trim()))) {
				list.add("默认提交");
				return list;
			}
		}

		for (PvmTransition tr : outTransitions) {
			String lineName = (String) tr.getProperty("name");
			if (lineName != null && !"".equals(lineName.trim())) {
				list.add(lineName);
			}
			// 继续获取下个节点
			PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
			// 如果是排他网关
			if ("exclusiveGateway".equals(ac.getProperty("type"))) {
				outTransitionsTemp = ac.getOutgoingTransitions();
				if (outTransitionsTemp == null
						|| outTransitionsTemp.size() == 0)
					continue;
				// 只获取一级，一步一步往后执行
				for (PvmTransition tr1 : outTransitionsTemp) {
					String name = (String) tr1.getProperty("name");
					if (name != null && !"".equals(name.trim())) {
						list.add(name.trim());
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 启动流程实例，让启动的流程实例关联业务
	 * @author Jeffrey
	 * @since 2017年3月30日 上午10:21:13
	 * @param busiId 业务表单id
	 * @param classSimpleName 对应业务类名
	 * @param nextAssignee 下一个处理人用户名（唯一）
	 */
	public void saveStartProcess(Integer busiId, String classSimpleName, String nextAssignee) {
		Map<String, Object> variables = new HashMap<String, Object>();
		// 使用表达式的方式设置下个办理人 从Session中获取当前任务的办理人，使用流程变量设置下一个任务的办理人 inputUser是流程变量的名称，
		variables.put("inputUser", nextAssignee); // 表示惟一用户
		/**
		 * (1)使用流程变量设置字符串（格式：LeaveBill.id的形式），通过设置，让启动的流程（流程实例）关联业务
		 * (2)使用正在执行对象表中的一个字段BUSINESS_KEY（Activiti提供的一个字段），让启动的流程（流程实例）关联业务
		 */
		// 格式：LeaveBill.id的形式（使用流程变量）
		String objId = classSimpleName + "." + busiId;
		variables.put("objId", objId);
		// 使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(classSimpleName, objId, variables);
	}
	
	/**
	 * 根据任务id完成任务（需要完善）
	 * @author Jeffrey
	 * @since 2017年3月29日 下午9:25:35
	 * @param taskId 任务ID
	 * @param busiId 业务id
	 * @param outcome 连线的名称
	 * @param message 批注信息
	 */
	public void saveSubmitTask(String taskId, String outcome, String message) {
		/**
		 * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
		 */
		// 使用任务ID，查询任务对象，获取流程流程实例ID
		Task task = taskService.createTaskQuery()
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// Authentication.setAuthenticatedUserId(SessionContext.get().getName());
		taskService.addComment(taskId, processInstanceId, "【" + outcome + "】 " + message);
		/**
		 * 2：如果连线的名称是“默认提交”，那么就不需要设置，如果不是，就需要设置流程变量 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
		 * 流程变量的名称：outcome 流程变量的值：连线的名称
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		if (outcome != null && !outcome.equals("默认提交")) {
			variables.put("action", outcome);
		}

		// 3：使用任务ID，完成当前人的个人任务，同时设置流程变量
		taskService.complete(taskId, variables);
		// 4：当任务完成之后，需要指定下一个任务的办理人（使用类）-----已经开发完成

		/**
		 * 5：在完成任务之后，判断流程是否结束 如果流程结束了，更新对应业务的状态
		 */
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 流程结束了
		if (pi == null) {
			// 更新请假单表的状态从1变成2（审核中-->审核完成）
			//LeaveBill bill = leaveBillDao.findLeaveBillById(id);
			//bill.setState(2);
		}
	}
	
	/**
	 * 完成任务
	 * @author Jeffrey
	 * @since 2017年3月30日 上午10:43:25
	 * @param busiId 业务表单id
	 * @param actionName 动作名称如‘拒绝’ 要和流程图中的流程变量保持一致
	 * @param userName 审核人用户名
	 * @param classSimpleName 对应业务类名
	 * @param message 批注信息
	 */
	public void saveSubmitTask(Integer busiId, String actionName, String userName, String classSimpleName, String message) {
		// 使用任务ID，查询任务对象，获取流程流程实例ID
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
					.processInstanceBusinessKey(classSimpleName + "." + busiId)
					.singleResult();
		if (pi == null) {
			logger.info("业务id:" + busiId + "所对应流程已结束，动作：【" + actionName + "】");
			return;
		}
		Task task = taskService.createTaskQuery()
				.processInstanceId(pi.getId())// 使用流程实例ID查询
				.singleResult();
		// 在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
		Authentication.setAuthenticatedUserId(userName);
		taskService.addComment(task.getId(), pi.getId(), message);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("action", actionName);
		taskService.setVariableLocal(task.getId(), "actionResult", actionName);
		taskService.setVariableLocal(task.getId(), "taskName", task.getName());
		// 使用任务ID，完成当前人的个人任务，同时设置流程变量
		taskService.complete(task.getId(), variables);
		// 当任务完成之后，需要指定下一个任务的办理人（使用类Listener）-----已经开发完成
	}
}
