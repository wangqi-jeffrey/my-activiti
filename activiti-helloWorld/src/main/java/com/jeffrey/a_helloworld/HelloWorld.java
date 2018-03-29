package com.jeffrey.a_helloworld;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * 流程的基本操作
 * @author Jeffrey
 * @since 2017年2月28日 上午10:19:55
 */
public class HelloWorld {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**
	 * 部署流程定义
	 * @author Jeffrey
	 * @since 2017年2月28日 上午10:29:01
	 */
	@Test
	public void deploymentProcessDefinition() {
		Deployment deployment = processEngine.getRepositoryService() // 与流程定义和部署对象相关的Service
						.createDeployment() // 创建一个部署对象
						.name("helloworld入门程序") // 添加部署的名称
						.addClasspathResource("diagrams/helloworld.bpmn") // 从classpath的资源中加载，一次只能加载一个文件
						.addClasspathResource("diagrams/helloworld.png") // 从classpath的资源中加载，一次只能加载一个文件
						.deploy(); // 完成部署
		System.out.println("#######################################");
		System.out.println("部署id:" + deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	
	/**
	 * 启动流程实例
	 * @author Jeffrey
	 * @since 2017年2月28日 上午10:38:45
	 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "helloworld";
		ProcessInstance pi = processEngine.getRuntimeService() // 与正在执行的流程实例和执行对象相关的Service
						.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例id:" + pi.getId());
		System.out.println("流程部署id:" + pi.getDeploymentId());
		System.out.println("流程定义id:" + pi.getProcessDefinitionId());
	}
	
	/**
	 * 查询当前个人任务
	 * @author Jeffrey
	 * @since 2017年2月28日 上午10:46:06
	 */
	@Test
	public void findMyPersonalTask() {
		String assignee = "李四";
		List<Task> list = processEngine.getTaskService() // 与正在执行的任务管理相关的Service
						.createTaskQuery() // 创建任务查询对象
						.taskAssignee(assignee) // 指定个人任务查询，指定办理人
						.list();
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务id：" + task.getId());
				System.out.println("任务名称：" + task.getName());
				System.out.println("任务创建时间：" + task.getCreateTime());
				System.out.println("任务的办理人：" + task.getAssignee());
				System.out.println("流程实例id：" + task.getProcessInstanceId());
				System.out.println("执行对象id：" + task.getExecutionId());
				System.out.println("流程定义id:" + task.getProcessDefinitionId());
				System.out.println("##############################################");
			}
		}
		System.out.println("-----------------------结束-----------------------");
	}
	
	/**
	 * 完成我的任务
	 * @author Jeffrey
	 * @since 2017年2月28日 上午10:58:14
	 */
	@Test
	public void completeMyPersonalTask() {
		String taskId = "37504";
		processEngine.getTaskService().complete(taskId);
	}
}
