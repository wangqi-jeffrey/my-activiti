package cn.itcast.ssh.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 预约登记审批，只获取业务相关的用户
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	private FixedValue tag;
	@Override
	public void notify(DelegateTask delegateTask) {
		System.out.println("-----------------------ManagerTaskHandler---------------------------");
		System.out.println("---------------------delegateTask.getEventName():" + delegateTask.getEventName());
		System.out.println("---------------------delegateTask.getAssignee():" + delegateTask.getAssignee());
		// 先找有没有上一个办理人，如果有，指定上一个办理人为当前办理人
		WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
		HistoryService historyService = (HistoryService) ac.getBean("historyService");
		
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		// 从历史任务中查找当前任务的上个办理人，并将当前任务指定给该办理人
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()//创建历史任务实例查询
				.processInstanceId(delegateTask.getProcessInstanceId())
				.finished() // 查询已完成的
				.taskDefinitionKey(taskDefinitionKey)
				.list();
		String message = "";
		String userid = "";
		String tagText = "使用历史办理人";
		if(list!=null && list.size()>0){
			for(HistoricTaskInstance hti:list){
				System.out.println(hti.getAssignee()+"===="+hti.getTaskDefinitionKey()+"--"+hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());
				System.out.println("################################");
			}
			userid = list.get(0).getAssignee();
			message = "=======从历史中获取到办理人==========";
		} else {
			// 随机指定下一个办理人
			tagText = String.valueOf(tag.getValue(delegateTask));
			if ("1".equals(tagText)) {
				// 业务总监
				List<String> list1 = new ArrayList<String>();
				list1.add("2");
				list1.add("3");
				list1.add("4");
				list1.add("5");
				list1.add("6");
				userid = list1.get(new Random().nextInt(list1.size()));
			}
			if ("2".equals(tagText)) {
				// 风控总监
				List<String> list2 = new ArrayList<String>();
				list2.add("8");
				list2.add("9");
				list2.add("10");
				list2.add("11");
				userid = list2.get(new Random().nextInt(list2.size()));
			}
		}
		SessionContext.setNextAssignee(userid);
		System.out.println("ManagerTaskHandler当前标识：" + tagText + "，下一个办理人：" +SessionContext.getNextAssignee()+",id"+ userid);
		
		delegateTask.setAssignee(userid);
	}
	
}
