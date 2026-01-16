package com.stech.apigateway.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoggingConfiguration {

    @Value("${spring.application.name:API-GATEWAY}")
    private String appName;

    @PostConstruct
    public void configureLogback() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Check if appenders already exist to avoid duplication on refresh
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        if (rootLogger.getAppender("FILE") != null) {
            return;
        }

        log.info("Configuring Logback for File logging for app: {}", appName);

        // --- File Appender ---
        String logDir = "../../logs";
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("FILE");
        fileAppender.setFile(logDir + "/" + appName + ".log");

        // Rolling Policy
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(logDir + "/" + appName + ".%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.start();

        fileAppender.setRollingPolicy(rollingPolicy);

        // File Encoder
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(context);
        fileEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [RID:%X{requestId}] %msg%n");
        fileEncoder.start();

        fileAppender.setEncoder(fileEncoder);
        fileAppender.start();

        // --- Add to Root Logger ---
        // rootLogger.addAppender(lokiAppender);
        rootLogger.addAppender(fileAppender);
        
        log.info("Logback configuration completed for API Gateway.");
    }
}
