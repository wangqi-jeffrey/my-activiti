package com.neo.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neo.entity.ActComment;
import com.neo.entity.ApprovalProcess;
import com.neo.service.WorkflowService;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	@Autowired
	private WorkflowService workflowService;

	/**
	 * 部署管理首页显示
	 * 
	 * @return
	 */
	@GetMapping("/deployHome")
	public Object deployHome() {
		return workflowService.findDeployments();
	}

	/**
	 * 查看流程定义的流程图
	 * @author Jeffrey
	 * @since 2017年3月29日 下午4:16:55
	 * @param deploymentId 部署对象ID
	 * @param imageName 资源图片名称
	 * @throws IOException 
	 */
	@GetMapping("/viewImage")
	public void viewImage(String deploymentId, String imageName, HttpServletResponse response) throws IOException {
		// 1：获取页面传递的部署对象ID和资源图片名称
		// 2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = workflowService.findImageInputStream(deploymentId, imageName);
		// 3：从response对象获取输出流
		OutputStream out = response.getOutputStream();
		// 4：将输入流中的数据读取出来，写到输出流中
		for (int b = -1; (b = in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();
	}
	
	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 * @author Jeffrey
	 * @since 2017年3月29日 下午6:40:55
	 * @param taskId 任务id
	 */
	@GetMapping("/viewCurrentImage")
	public Object viewCurrentImage(String taskId) {
		/** 一：查看流程图 */
		// 1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deploymentId", pd.getDeploymentId());
		map.put("imageName", pd.getDiagramResourceName());
		/** 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中 */
		Map<String, Object> coordinates = workflowService.findCoordingByTask(taskId);
		map.put("coordinates", coordinates);
		return map;
	}
	
	/**
	 * 获取当前用户待办任务
	 * @author Jeffrey
	 * @since 2017年3月29日 下午4:40:44
	 * @return
	 */
	@GetMapping("/todoTask")
	public Object listTask(String name) { // 后续去掉
		// 1：从Session中获取当前用户名
		String userName = name;
		// 2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>
		return workflowService.findTaskListByName(userName);
	}
	
	/**
	 * 查看历史的批注信息
	 * @author Jeffrey
	 * @since 2017年3月29日 下午6:19:15
	 * @param id 业务id
	 * @return
	 */
	@GetMapping("/viewHisComment")
	public Object viewHisComment(Integer id) {
		List<ActComment> commentList = workflowService.findCommentByTag(id, ApprovalProcess.class.getSimpleName());
		return commentList;
	}
	
	/**
	 * 完成任务
	 * @author Jeffrey
	 * @since 2017年3月30日 上午11:10:22
	 * @param busiId
	 * @param actionName
	 * @param userName
	 * @param message
	 * @return
	 */
	@GetMapping("/completeTask")
	public Object completeTask(Integer busiId, String actionName, String userName, String message) {
		workflowService.saveSubmitTask(busiId, actionName, userName, ApprovalProcess.class.getSimpleName(), message);
		return "ok";
	}
}
