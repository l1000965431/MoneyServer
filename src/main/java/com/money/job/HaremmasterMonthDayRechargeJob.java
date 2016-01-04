package com.money.job;

import com.money.Service.Haremmaster.HaremmasterService;
import com.money.Service.ServiceFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import until.MoneyServerDate;

import java.util.Date;

/**
 * 群主每月每日结算任务
 */
public class HaremmasterMonthDayRechargeJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaremmasterMonthDayRechargeJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        LOGGER.info( "群主每日每月结算定时器" );

        HaremmasterService haremmasterService = ServiceFactory.getService( "HaremmasterService" );

        if( haremmasterService != null ){
            //定时器是在第二天凌晨 需要计算前一天的时间
            Date preDate = MoneyServerDate.getDatePreDate();
            haremmasterService.SettlementMonthDay( preDate );
        }
    }
}
