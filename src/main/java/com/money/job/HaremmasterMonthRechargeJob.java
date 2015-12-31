package com.money.job;

import com.money.Service.Haremmaster.HaremmasterService;
import com.money.Service.ServiceFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 群主每月结算任务
 */
public class HaremmasterMonthRechargeJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaremmasterMonthRechargeJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.error( "群主每月结算定时器" );
        HaremmasterService haremmasterService = ServiceFactory.getService( "HaremmasterService" );

        if( haremmasterService != null ){
            haremmasterService.SettlementMonth();
        }
    }
}
