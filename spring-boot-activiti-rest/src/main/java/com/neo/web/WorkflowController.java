package com.neo.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class WorkflowController {

	@Autowired
	private RepositoryService repositoryService;
	/**
	 * 部署管理首页显示
	 * @return
	 */
	@GetMapping("/deployHome")
	public Object deployHome() {
		// 1:查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> depList = repositoryService.createDeploymentQuery() // 创建部署对象查询
				.orderByDeploymenTime().asc()
				.list();
		
		// 2:查询流程定义的信息，对应表（act_re_procdef）
		List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery() // 创建流程定义查询
				.orderByProcessDefinitionVersion().asc()
				.list();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("depList", depList);
		map.put("pdList", pdList);
		//System.out.println(depList.size() + "----" + pdList.size());
		for (Deployment deployment : depList) {
			System.out.println(deployment);
			System.err.println(deployment.getName());
		}
		return pdList;
	}
}
