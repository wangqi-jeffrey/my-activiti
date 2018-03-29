package com.system.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MainController implements InitializingBean{

    protected Log logger = LogFactory.getLog(getClass());

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("系统已启动......");
       
    }

}
