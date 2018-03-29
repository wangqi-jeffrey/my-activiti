package com.system.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.flow.ActProcessDefinition;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.system.service.FlowService;
import java.util.Map;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;

@RestController
@RequestMapping("/flow")
public class FlowController {

    protected Log logger = LogFactory.getLog(getClass());
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    private FlowService flowService;
    @Autowired
    private HistoryService historyService;

    @RequestMapping(value = "findModel", method = RequestMethod.POST)
    public Object findModel(String name, int page, int limit) {//查询模型
        ModelMap mm = new ModelMap();
        List<Model> mlist = new ArrayList();
        long total = 0;
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if (StringUtils.isEmpty(name)) {
            mlist = modelQuery.listPage(page - 1, limit);
            total = modelQuery.count();
        } else {
            mlist = modelQuery.modelNameLike("%" + name + "%").listPage(page - 1, limit);
            total = modelQuery.count();
        }
        mm.addAttribute("total", total);
        mm.addAttribute("datas", mlist);
        return mm;
    }

    @RequestMapping(value = "createModel")
    public Object createModel(String name, String key, String description) {// 创建模型
        return flowService.createModel(name, key, description);
    }

    @RequestMapping(value = "importModel")
    public Object importModel(@RequestParam(value = "file", required = false) MultipartFile file) {// 导入模型
        return flowService.importModel(file);
    }

    @RequestMapping(value = "exportByModelId")
    public ResponseEntity<Byte[]> exportByModelId(String id) throws IOException {// 模型导出为xml文件
        Model modelData = repositoryService.getModel(id);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        String filename = bpmnModel.getMainProcess().getId() + ".bpmn";

        byte[] bytes = IOUtils.toByteArray(in);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", new String(filename.getBytes("utf-8"), "ISO8859-1"));
        return new ResponseEntity(bytes, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "showPicByModelId")// 通过模型ID生成流程图片
    public void showPicByModelId(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        Model modelData = repositoryService.getModel(id);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        try ( //打印流程图
                InputStream imageStream = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator().generateDiagram(bpmnModel, "PNG",
                        processEngineConfiguration.getActivityFontName(),
                        processEngineConfiguration.getLabelFontName(),
                        processEngineConfiguration.getClassLoader(),
                        1.0)) {
            byte[] buffer = new byte[4 * 1024];
            int length;
            while ((length = imageStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, length);
            }
        }
        response.getOutputStream().close();
    }

    @RequestMapping(value = "showXMLByModelId")// 通过模型ID生成流程图片
    public void showXMLByModelId(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        Model modelData = repositoryService.getModel(id);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bpmnBytes);
        byte[] buffer = new byte[4 * 1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            response.getOutputStream().write(buffer, 0, length);
        }
        response.getOutputStream().close();
    }

    @RequestMapping(value = "deleteModel", method = RequestMethod.POST)
    public Object deleteModel(@RequestParam String[] ids) {//删除模型
        return flowService.deleteModel(ids);
    }

    @RequestMapping(value = "updateModelName")
    public Object updateModelName(String name, String id) {// 改变模型的名称
        return flowService.updateModelName(name, id);
    }

    @RequestMapping(value = "findProcessDefinition")
    public Object findProcessDefinition(String name, int page, int limit) {// 获取所有的流程列表
        ModelMap mm = new ModelMap();
        List<ActProcessDefinition> list = new ArrayList();
        List<ProcessDefinition> processDefinitionList = new ArrayList();
        long total = 0;
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isEmpty(name)) {
            processDefinitionList = processDefinitionQuery.orderByDeploymentId().desc().listPage(page - 1, limit);
            total = processDefinitionQuery.count();
        } else {
            processDefinitionList = processDefinitionQuery.processDefinitionNameLike("%" + name + "%").orderByDeploymentId().desc().listPage(page - 1, limit);
            total = processDefinitionQuery.count();
        }

        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            ActProcessDefinition pd = flowService.cloneProcessDefinition(processDefinition);
            pd.setDeploymentTime(deployment.getDeploymentTime());
            list.add(pd);
        }
        mm.addAttribute("total", total);
        mm.addAttribute("datas", list);
        return mm;
    }

    @RequestMapping(value = "updateState")
    public Object updateState(String state, String processDefinitionId) {// 改变流程的状态
        return flowService.updateState(state, processDefinitionId);
    }

    @RequestMapping(value = "updateProcessDefinitionName")
    public Object updateProcessDefinitionName(String name, String id) {// 改变流程定义的名称
        return flowService.updateProcessDefinitionName(name, id);
    }

    @RequestMapping(value = "convertToModel")
    public Object convertToModel(String processDefinitionId) {// 流程定义转换为模型(创建新模型)
        return flowService.convertToModel(processDefinitionId);
    }

    @RequestMapping(value = "deleteProcessDefinition", method = RequestMethod.POST)
    public Object deleteProcessDefinition(@RequestParam String[] ids) {//删除流程定义
        return flowService.deleteProcessDefinition(ids);
    }

    @RequestMapping(value = "deployByFile")
    public Object deployByFile(@RequestParam(value = "file", required = false) MultipartFile file) {// 部署流程
        return flowService.deployByFile(file);
    }

    @RequestMapping(value = "exportByProcessDefinitionId")
    public ResponseEntity<byte[]> exportByProcessDefinitionId(@RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam("resourceName") String resourceName) throws IOException {// 在流程定义导出为xml文件
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resourceName);

        byte[] bytes = IOUtils.toByteArray(resourceAsStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", new String(resourceName.getBytes("utf-8"), "ISO8859-1"));
        return new ResponseEntity(bytes, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "resourceRead")//读取流程定义的xml文件
    public void resourceRead(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("resourceName") String resourceName, HttpServletResponse response) throws Exception {
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        resourceName = new String(resourceName.getBytes("ISO-8859-1"), "UTF-8");
        InputStream resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resourceName);
        byte[] buffer = new byte[4 * 1024];
        int length;
        while ((length = resourceAsStream.read(buffer)) > 0) {
            response.getOutputStream().write(buffer, 0, length);
        }
        response.getOutputStream().close();
    }
       @RequestMapping(value = "deployByModelId")
    public Object deployByModelId(String id) {// 通过模型Id部署流程
        return flowService.deployByModelId(id);
    }
    
               @RequestMapping(value = "startFlow", method = RequestMethod.POST)
    public Object startFlow(String userId,String businessKey,String keyName) {// 开始流程
        return flowService.startFlow(userId,businessKey,keyName);
    }
    
        @RequestMapping(value = "/findHistoricProcessInstance", method = RequestMethod.POST)
    public Map<String, Object> findHistoricProcessInstance(int page, int limit) {//查找历史流程实例
        return flowService.findHistoricProcessInstance(page,limit);
    }
    @RequestMapping(value = "/deleteProcessInstance", method = RequestMethod.POST)
    public Map<String, Object> deleteProcessInstance(String[] ids) {//删除流程实例
        return flowService.deleteProcessInstance(ids);
    }
    
    @RequestMapping(value = "traceProcess")// 读取带跟踪的图片
    public void traceProcess(
            @RequestParam("processInstanceId") String processInstanceId,
            HttpServletResponse response) throws Exception {
        logger.info("进入了 traceProcess方法");
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        logger.info("historicProcessInstance:" + historicProcessInstance);
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> activitiIds = new ArrayList();
        List<String> flowIds = new ArrayList();

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
        flowIds = flowService.getHighLightedFlows(processDefinition, activityInstances);//获取流程走过的线

        for (HistoricActivityInstance hai : activityInstances) {
            activitiIds.add(hai.getActivityId());//获取流程走过的节点
        }
        try ( //打印流程图
                InputStream imageStream = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator().generateDiagram(bpmnModel, "PNG", activitiIds, flowIds,
                        processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), processEngineConfiguration.getClassLoader(), 1.0)) {
            byte[] buffer = new byte[4 * 1024];
            int length;
            while ((length = imageStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, length);
            }
        }
        response.getOutputStream().close();
        
    }

}
