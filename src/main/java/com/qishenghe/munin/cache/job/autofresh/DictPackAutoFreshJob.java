package com.qishenghe.munin.cache.job.autofresh;

import com.qishenghe.munin.session.MuninSession;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * 字典容器自刷新Job
 *
 * @author qishenghe
 * @date 2021/6/7 15:43
 * @change 2021/6/7 15:43 by qishenghe for init
 */
public class DictPackAutoFreshJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        // 取出session对象
        MuninSession muninSession = (MuninSession) jobDataMap.get("session");
        // 执行刷新
        muninSession.refreshPack();
    }
}
