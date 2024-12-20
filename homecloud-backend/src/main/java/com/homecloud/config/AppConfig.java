package com.homecloud.config;

import com.homecloud.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${dev:false}")
    private Boolean dev;

    public String getProjectFolder() {
        if (!StringUtil.isEmpty(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }

    public static Logger getLogger() {
        return logger;
    }


}
