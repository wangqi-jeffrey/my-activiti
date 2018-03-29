package com.system.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    protected Log logger = LogFactory.getLog(getClass());
    @Autowired
    private ObjectMapper objectMapper;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseBody
    public Object DataIntegrityViolationException(DataIntegrityViolationException e) {//判断重复数据插入
        ModelMap mm = new ModelMap();
        logger.error("DataIntegrityViolationException:" + e.getLocalizedMessage());
        logger.error("DataIntegrityViolationException:", e);
        String msg = "数据提交失败,不允许插入重复数据";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    @ResponseBody
    public Object EmptyResultDataAccessException(EmptyResultDataAccessException e) {//判断数据删除时是否存在要删除的数据(通过ID查找)
        ModelMap mm = new ModelMap();
        logger.error("EmptyResultDataAccessException:" + e.getLocalizedMessage());
        logger.error("DataIntegrityViolationException:", e);
        String msg = "数据操作失败,找不到指定数据";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

    @ExceptionHandler({ActivitiObjectNotFoundException.class})
    @ResponseBody
    public Object ActivitiObjectNotFoundException(ActivitiObjectNotFoundException e) {//
        ModelMap mm = new ModelMap();
        String msg = "工作流数据操作失败,找不到指定数据";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

    @ExceptionHandler({ActivitiException.class})
    @ResponseBody
    public Object ActivitiException(ActivitiException e) {//
        ModelMap mm = new ModelMap();
        String msg = "工作流数据操作异常";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public Object NullPointerException(NullPointerException e) {//空指针异常
        ModelMap mm = new ModelMap();
        logger.error("NullPointerException:" + e.getLocalizedMessage());
        logger.error("DataIntegrityViolationException:", e);
        String msg = "数据操作失败,后台触发空指针异常";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

    @ExceptionHandler({IOException.class})
    @ResponseBody
    public Object IOException(IOException e) {//文件输入输出异常
        ModelMap mm = new ModelMap();
        logger.error("IOException:" + e.getLocalizedMessage());
        logger.error("DataIntegrityViolationException:", e);
        String msg = "文件操作失败,请稍后再试";
        mm.put("msg", msg);
        mm.put("success", false);
        return mm;
    }

}
