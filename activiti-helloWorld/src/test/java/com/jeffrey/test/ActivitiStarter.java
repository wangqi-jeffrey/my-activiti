package com.jeffrey.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class ActivitiStarter {

	/**
	 * 使用代码创建数据库表
	 * @author Jeffrey
	 * @since 2017年2月28日 上午9:44:17
	 */
	@Test
	public void createTables() {
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnicode=true&characterEncoding=UTF-8");
		processEngineConfiguration.setJdbcUsername("root");
		processEngineConfiguration.setJdbcPassword("root");
		
		/**
	 		public static final String DB_SCHEMA_UPDATE_FALSE = "false";不能自动创建表，需要表存在
			public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";先删除表再创建表
			public static final String DB_SCHEMA_UPDATE_TRUE = "true";如果表不存在，自动创建表
		 */
		processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		// 工作流的核心对象，ProcessEnginee对象
		ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
		
		System.out.println("processEngine:" + processEngine);
	}
	
	/**
	 * 使用配置文件创建工作流所需要的表
	 * @author Jeffrey
	 * @since 2017年2月28日 上午10:00:28
	 */
	@Test
	public void createTablesByXML() {
//		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
		ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
		System.out.println("xml processEngine:" + processEngine);
	}
}
