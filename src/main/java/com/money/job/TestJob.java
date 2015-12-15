package com.money.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import until.MoneyServerDate;
import until.ScheduleJob;

import java.io.Serializable;

/**
 * Created by liumin on 15/7/29.
 */


public class TestJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestJob.class);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("任务成功运行"+":TestJob1"+ MoneyServerDate.getStringCurDate());
        LOGGER.error( "任务成功运行"+":TestJob1"+ MoneyServerDate.getStringCurDate() );
        /*ScheduleJob scheduleJob = (ScheduleJob)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]");*/
    }
}
