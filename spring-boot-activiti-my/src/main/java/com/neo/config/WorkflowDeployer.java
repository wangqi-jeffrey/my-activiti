package com.neo.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * 结合Spring自动部署Activit流程定义文件
 * 
 * @author Jeffrey
 * @since 2017年4月7日 下午7:04:52
 */
//@Component
//public class WorkflowDeployer implements InitializingBean {
//
//	private final Logger logger = LoggerFactory
//			.getLogger(WorkflowDeployer.class);
//
//	@Autowired
//	private ResourceLoader resourceLoader;
//	@Autowired
//	private RepositoryService repositoryService;
//	private ResourcePatternResolver rpr = ResourcePatternUtils
//									.getResourcePatternResolver(resourceLoader);
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		Resource[] deploymentResources = rpr.getResources("classpath*:diagrams/*.bpmn");
//		logger.info("---流程文件数量:" + deploymentResources.length);
//		
//		if (deploymentResources != null || deploymentResources.length == 0) {
//			for (Resource r : deploymentResources) {
//				String deploymentName = "Deployment_" + r.getFilename();
//				String resourceName = r.getFilename();
//				boolean doDeploy = true;
//				List<Deployment> deployments = repositoryService
//										.createDeploymentQuery().deploymentName(deploymentName)
//										.orderByDeploymenTime().desc().list();
//				if (deployments != null && !deployments.isEmpty()) {
//					logger.info("---存在部署历史 : " + r.getFilename());
//					Deployment existing = deployments.get(0);
//					try {
//						InputStream in = repositoryService.getResourceAsStream(existing.getId(), resourceName);
//						if (in != null) {
//							File f = File.createTempFile("deployment", "bpmn", new File(System.getProperty("java.io.tmpdir")));
//							f.deleteOnExit();
//							OutputStream out = new FileOutputStream(f);
//							IOUtils.copy(in, out);
//							in.close();
//							out.close();
//							doDeploy = (FileUtils.checksumCRC32(f) != FileUtils.checksumCRC32(r.getFile()));
//							logger.info("---文件比对结果（true:不相同，需要重新部署，false:相同）：" + doDeploy);
//						} else
//							throw new ActivitiException("不能读取资源 " + resourceName + ", 输入流为空");
//					} catch (ActivitiException ex) {
//						logger.error("Unable to read " + resourceName
//											+ " of deployment " + existing.getName()
//											+ ", id: " + existing.getId()
//											+ ", will re-deploy");
//					}
//				}
//				if (doDeploy) {
//					repositoryService.createDeployment().name(deploymentName)
//										.addInputStream(resourceName, r.getInputStream())
//										.deploy();
//					logger.info("---部署成功 : " + r.getFilename());
//				} else {
//					logger.info("---已被部署: " + r.getFilename());
//				}
//			}
//		} else {
//			logger.info("---没有需要部署的流程文件---");
//		}
//	}
// }
