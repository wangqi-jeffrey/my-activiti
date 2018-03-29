package com.system.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class FlowHandler implements TaskListener, ExecutionListener, JavaDelegate {//合同申请流程处理 //ActivitiEventListener

    protected Log logger = LogFactory.getLog(getClass());


    @Override
    public void notify(DelegateTask delegateTask) {//任务监听事件 activiti:taskListener
        logger.info("进入了 FlowHandler 的 activiti:taskListener START");

        String eventName = delegateTask.getEventName();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        String processInstanceId = delegateTask.getProcessInstanceId();
        String name = delegateTask.getName();
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        logger.info("eventName:" + eventName);
        logger.info("name:" + name);
        logger.info("processDefinitionId:" + processDefinitionId);
        logger.info("processInstanceId:" + processInstanceId);
        logger.info("taskDefinitionKey:" + taskDefinitionKey);

        
        logger.info("进入了 FlowHandler 的 activiti:taskListener END");
    }

    @Override
    public void notify(DelegateExecution de) throws Exception {//执行监听事件  activiti:executionListener
        logger.info("进入了 FlowHandler 执行监听事件  activiti:executionListener START");
        String currentActivityId = de.getCurrentActivityId();
        String eventName = de.getEventName();
        String processDefinitionId = de.getProcessDefinitionId();
        String processInstanceId = de.getProcessInstanceId();
        String businessKey = de.getProcessBusinessKey();
        String currentActivityName = de.getCurrentActivityName();

        logger.info("currentActivityId:" + currentActivityId);
        logger.info("currentActivityName:" + currentActivityName);
        logger.info("eventName:" + eventName);
        logger.info("processDefinitionId:" + processDefinitionId);
        logger.info("processInstanceId:" + processInstanceId);
        logger.info("businessKey:" + businessKey);
        logger.info("进入了 FlowHandler 执行监听事件  activiti:executionListener END");

    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("进入了 FlowHandler 类的javaService的监听方法 START");
        logger.info("getCurrentActivityId" + execution.getCurrentActivityId());
        logger.info("getCurrentActivityName" + execution.getCurrentActivityName());
        logger.info("getEventName" + execution.getEventName());
        logger.info("getProcessBusinessKey" + execution.getProcessBusinessKey());
        logger.info("getProcessDefinitionId" + execution.getProcessDefinitionId());
        logger.info("getProcessInstanceId" + execution.getProcessInstanceId());
        logger.info("进入了 FlowHandler 类的javaService的监听方法 END");
    }

}
