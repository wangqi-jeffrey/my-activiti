package cn.itcast.ssh.utils;

import java.util.List;
import java.util.Random;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.service.IEmployeeService;

/**
 * 预约登记审批，只获取业务相关的用户
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler1 implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		System.out.println("-----------------------ManagerTaskHandler1---------------------------");
		System.out.println("---------------------delegateTask.getTaskDefinitionKey():" + delegateTask.getTaskDefinitionKey());
		System.out.println("---------------------delegateTask.getProcessInstanceId():" + delegateTask.getProcessInstanceId());
		System.out.println("---------------------delegateTask.getEventName():" + delegateTask.getEventName());
		System.out.println("---------------------delegateTask.getAssignee():" + delegateTask.getAssignee());
		
		WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
		HistoryService historyService = (HistoryService) ac.getBean("historyService");
		
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		// 从历史任务中查找当前任务的上个办理人，并将当前任务指定给该办理人
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()//创建历史任务实例查询
				.processInstanceId(delegateTask.getProcessInstanceId())
				.finished() // 查询已完成的
				.taskDefinitionKey(taskDefinitionKey)
				.list();
		String userid = "";
		String message = "";
		if(list!=null && list.size()>0){
			for(HistoricTaskInstance hti:list){
				System.out.println(hti.getAssignee()+"===="+hti.getTaskDefinitionKey()+"--"+hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());
				System.out.println("################################");
			}
			userid = list.get(0).getAssignee();
			message = "=======从历史中获取到办理人==========";
		} else {
			// 切换角色
			if ("usertask4".equals(taskDefinitionKey)) {
				// 随机指定一个风控经理
				String[] str = {"7","17","18"};
				userid = str[new Random().nextInt(str.length)];
			} else if ("usertask6".equals(taskDefinitionKey)) {
				// 随机指定一个财务经理
				String[] str = {"12","19","20"};
				userid = str[new Random().nextInt(str.length)];
			} else {
				// 如果没有历史任务，则将当前用户指定为下个办理人
				message = "----------该任务节点没有历史办理人-------------将当前用户指定为下个办理人";
				Employee employee = SessionContext.get();
				userid = employee.getId() + "";
			}
		}
		SessionContext.setNextAssignee(userid);
		delegateTask.setAssignee(userid);
		System.out.println(message);
		System.out.println("当前标识：ManagerTaskHandler1，下一个办理人：" + SessionContext.getNextAssignee() + ",id: " + userid);
	}

}
