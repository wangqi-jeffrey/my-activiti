package com.anxpp.demo.activiti.simple.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 流程监听器
 * @author anxpp.com
 * 2016年12月24日 下午12:33:58
 */
public class SimpleProcessEndListener implements ExecutionListener{
	private static final long serialVersionUID = 5212042435691138021L;
	private final static Logger log = LoggerFactory.getLogger(SimpleProcessEndListener.class);
	@Override
	public void notify(DelegateExecution arg0) throws Exception {
		log.info("流程结束监听器...");
		//TODO 修改业务数据状态
	}
}
