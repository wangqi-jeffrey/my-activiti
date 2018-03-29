package cn.itcast.ssh.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 预约登记审批，只获取业务相关的用户
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler1 implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		delegateTask.setAssignee("4");
	}

}
