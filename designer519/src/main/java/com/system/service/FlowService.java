package com.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.flow.ActHistoricProcessInstance;
import com.system.flow.ActProcessDefinition;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FlowService {

    protected Log logger = LogFactory.getLog(getClass());
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createModel(String name, String key, String description) {
        ModelMap mm = new ModelMap();
        String id = null;
        String msg = "模型创建成功";
        boolean flag = true;
        try {
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.putPOJO("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            description = StringUtils.defaultString(description);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,
                    description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(name);
            modelData.setKey(StringUtils.defaultString(key));

            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(),
                    editorNode.toString().getBytes("utf-8"));
            id = modelData.getId();
        } catch (Exception e) {
            msg = "模型创建失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("id", id);
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importModel(MultipartFile file) {// 模型导入
        ModelMap mm = new ModelMap();
        String msg = "模型导入成功";
        boolean flag = true;
        try {
            InputStream inputStream = file.getInputStream();
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xtr = xif.createXMLStreamReader(inputStream);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            BpmnJsonConverter converter = new BpmnJsonConverter();
            ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model modelData = repositoryService.newModel();
            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            logger.info("file.getName():" + file.getOriginalFilename());
            modelData.setName(file.getOriginalFilename().split("[.]")[0]);
            modelData.setMetaInfo(modelObjectNode.toString());
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
        } catch (IOException | XMLStreamException e) {
            msg = "模型导入失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteModel(String[] ids) {//删除模型
        ModelMap mm = new ModelMap();
        String msg = "数据删除成功";
        boolean flag = true;
        for (String id : ids) {
            repositoryService.deleteModel(id);
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public static String exportDiagramToFile(RepositoryService repositoryService, ProcessDefinition processDefinition, String exportDir) throws IOException {
        String diagramResourceName = processDefinition.getDiagramResourceName();
        String key = processDefinition.getKey();
        int version = processDefinition.getVersion();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
        byte[] b = new byte[resourceAsStream.available()];
        resourceAsStream.read(b, 0, b.length);
        String diagramDir = exportDir + "/" + key + "/" + version;
        File diagramDirFile = new File(diagramDir);
        if (!diagramDirFile.exists()) {
            diagramDirFile.mkdirs();
        }
        String diagramPath = diagramDir + "/" + diagramResourceName;
        File file = new File(diagramPath);
        if (file.exists()) {
            return diagramPath;
        } else {
            file.createNewFile();
        }
        FileUtils.writeByteArrayToFile(file, b);
        return diagramPath;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateModelName(String name, String id) {// 改变模型义的名称
        ModelMap mm = new ModelMap();
        String msg = "数据修改成功";
        boolean flag = true;
        try {
            Model model = repositoryService.getModel(id);
            if (model != null) {
                model.setName(name);
                ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(model.getMetaInfo());
                modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
                model.setMetaInfo(modelNode.toString());
                repositoryService.saveModel(model);
            }
        } catch (Exception e) {
            msg = "数据修改失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    public ActProcessDefinition cloneProcessDefinition(ProcessDefinition p) { //克隆流程定义
        ActProcessDefinition pd = new ActProcessDefinition();
        pd.setCategory(p.getCategory());
        pd.setDeploymentId(p.getDeploymentId());
        pd.setDescription(p.getDescription());
        pd.setDiagramResourceName(p.getDiagramResourceName());
        pd.setResourceName(p.getResourceName());
        pd.setId(p.getId());
        pd.setKeyName(p.getKey());
        pd.setName(p.getName());
        pd.setVersion(p.getVersion());
        pd.setSuspended(p.isSuspended());
        return pd;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateState(String state, String processDefinitionId) {// 改变流程的状态
        ModelMap mm = new ModelMap();
        String msg = "流程状态改变成功";
        boolean flag = true;
        try {
            switch (state) {
                case "active":
                    repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
                    break;
                case "suspend":
                    repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
                    break;
            }
        } catch (Exception e) {
            msg = "流程状态改变失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProcessDefinitionName(String name, String id) {// 改变流程定义的名称
        ModelMap mm = new ModelMap();
        String msg = "数据操作成功";
        boolean flag = true;
        try {
            String tableName = managementService.getTableName(ProcessDefinition.class);
            String sql = "update " + tableName + " set name_ =? where id_ =?";
            Object[] obj = new Object[]{name, id};
            jdbcTemplate.update(sql, obj);
        } catch (Exception e) {
            msg = "数据操作失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> convertToModel(String processDefinitionId) {
        ModelMap mm = new ModelMap();
        String msg = "流程转换成功";
        boolean flag = true;
        try {
            ProcessDefinition processDefinition = repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId).singleResult();
            InputStream bpmnStream = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(),
                    processDefinition.getResourceName());
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

            BpmnJsonConverter converter = new BpmnJsonConverter();
            ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model modelData = repositoryService.newModel();
            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelData.setMetaInfo(modelObjectNode.toString());
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

        } catch (XMLStreamException | UnsupportedEncodingException e) {
            msg = "流程转换失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteProcessDefinition(String[] ids) {//删除流程定义
        ModelMap mm = new ModelMap();
        String msg = "数据删除成功";
        boolean flag = true;
        for (String id : ids) {
            repositoryService.deleteDeployment(id, true);
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deployByFile(MultipartFile file) {// 部署流程
        ModelMap mm = new ModelMap();
        String msg = "流程部署成功";
        boolean flag = true;
        String fileName = file.getOriginalFilename();
        String exportDir = "/tmp/activiti";
        try {
            InputStream fileInputStream = file.getInputStream();
            Deployment deployment;
            String extension = FilenameUtils.getExtension(fileName);
            logger.info("extension:" + extension);
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            }
            List<ProcessDefinition> list = repositoryService
                    .createProcessDefinitionQuery()
                    .deploymentId(deployment.getId()).list();

            for (ProcessDefinition processDefinition : list) {
                exportDiagramToFile(repositoryService, processDefinition, exportDir);
            }

        } catch (Exception e) {
            msg = "流程部署失败,请稍后再试";
            flag = false;
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }
    public Map<String, Object> deployByModelId(String id) {//通过模型Id部署流程
        ModelMap mm = new ModelMap();
        String msg = "流程部署成功";
        boolean flag = true;
        try {
            Model modelData = repositoryService.getModel(id);
            ObjectNode modelNode = (ObjectNode) objectMapper.readTree(repositoryService.getModelEditorSource(modelData
                    .getId()));
            BpmnModel model = new BpmnJsonConverter()
                    .convertToBpmnModel(modelNode);
            String processName = modelData.getName() + ".bpmn";
            repositoryService.createDeployment().name(modelData.getName())
                    .addBpmnModel(processName, model).deploy();
        } catch (Exception e) {
            msg = "流程部署失败,请稍后再试";
            flag = false;
        }

        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> startFlow(String userId,String businessKey,String keyName) {
        //flowType 指明流程类型,对应的是流程定义文件里的流程表示
        ModelMap mm = new ModelMap();
        String msg = "流程启动成功"; //baseService.getValueByLocale("updateSucess");
        boolean flag = true;
        startWorkflow(userId, businessKey, keyName);//启动流程
        logger.info("流程启动成功");
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }
    
        @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startWorkflow(String userId, String businessKey, String keyName) {
        Map<String, Object> variables = new HashMap();
        return startWorkflow(userId, businessKey, keyName, variables);
    }
    
        @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startWorkflow(String userId, String businessKey, String keyName, Map<String, Object> variables) {
        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        logger.info("startWorkflow:" + userId);
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(keyName, businessKey, variables);
        return processInstance;
    }
    
    public Map<String, Object> findHistoricProcessInstance(int page, int limit) {//查找历史流程实例
        ModelMap mm = new ModelMap();
        List<ActHistoricProcessInstance> list = new ArrayList();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> hpiList = historicProcessInstanceQuery.listPage(page-1, limit);
        long total=historicProcessInstanceQuery.count();
        for (HistoricProcessInstance hpi : hpiList) {
            list.add(cloneHistoricProcessInstance(hpi));
        }
        mm.addAttribute("datas", list);
        mm.addAttribute("total", total);
        return mm;
    }
    public Map<String, Object> deleteProcessInstance(String[] ids) {//删除流程实例
        ModelMap mm = new ModelMap();
         String msg = "流程实例删除成功"; //baseService.getValueByLocale("updateSucess");
        boolean flag = true;
        for(String id:ids){
            ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            if(processInstance==null){
            historyService.deleteHistoricProcessInstance(id);
            }else{
            runtimeService.deleteProcessInstance(id, "流程作废");
            historyService.deleteHistoricProcessInstance(id);
            }
        }
        mm.addAttribute("success", flag);
        mm.addAttribute("msg", msg);
        return mm;
    }
    
    public ActHistoricProcessInstance cloneHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {//历史实例
        ActHistoricProcessInstance h = new ActHistoricProcessInstance();
        h.setBusinessKey(historicProcessInstance.getBusinessKey());
        h.setDeleteReason(historicProcessInstance.getDeleteReason());
        h.setDurationInMillis(historicProcessInstance.getDurationInMillis());
        h.setEndTime(historicProcessInstance.getEndTime());
        h.setId(historicProcessInstance.getId());
        h.setName(historicProcessInstance.getName());
        h.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
        h.setProcessVariables(historicProcessInstance.getProcessVariables());
        h.setStartActivityId(historicProcessInstance.getStartActivityId());
        h.setStartTime(historicProcessInstance.getStartTime());
        h.setStartUserId(historicProcessInstance.getStartUserId());
        h.setSuperProcessInstanceId(historicProcessInstance.getSuperProcessInstanceId());
        h.setTenantId(historicProcessInstance.getTenantId());
        return h;
    }

    public List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl
                    = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList();// 用以保存后需开始时间相同的节点 
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1).getActivityId());// 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点 
                if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {// 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2
                            = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {// 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }
}
