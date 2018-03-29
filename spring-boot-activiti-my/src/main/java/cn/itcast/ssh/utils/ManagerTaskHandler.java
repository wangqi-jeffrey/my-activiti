package cn.itcast.ssh.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

/**
 * 预约登记审批，只获取业务相关的用户
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	private FixedValue tag;
	@Override
	public void notify(DelegateTask delegateTask) {
		delegateTask.setAssignee("4");
	}
	
}
