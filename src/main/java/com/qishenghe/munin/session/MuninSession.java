package com.qishenghe.munin.session;

import com.qishenghe.munin.banner.MuninBannerPrinter;
import com.qishenghe.munin.cache.job.DictPackInitJob;
import com.qishenghe.munin.cache.job.autofresh.DictPackAutoFreshJob;
import com.qishenghe.munin.cache.pack.DictEntity;
import com.qishenghe.munin.cache.pack.DictPack;
import com.qishenghe.munin.util.DictCtrlUtil;
import com.qishenghe.munin.util.DictTransUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;

/**
 * 字典控制会话
 *
 * @author qishenghe
 * @date 2021/6/5 18:07
 * @change 2021/6/5 18:07 by qishenghe for init
 */
@Data
public class MuninSession {

    /**
     * 字典缓存总容器
     */
    private DictPack dictPack;

    /**
     * 多源字典加载互斥标记
     */
    private Boolean dictPackMutex;

    /**
     * 字典缓存容器初始化Job
     */
    private List<DictPackInitJob> dictPackInitJobs;

    /**
     * 自刷新调度器
     */
    private Scheduler dictPackAutoFreshScheduler;

    /**
     * 自刷新周期Cron表达式
     */
    private String autoRefreshCron;

    /**
     * 字典控制工具
     */
    private DictCtrlUtil dictCtrlUtil;

    /**
     * 字典数据转换工具
     */
    private DictTransUtil dictTransUtil;

    /**
     * 获取builder
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 刷新缓存容器
     *
     * @param dictPackMutex 互斥标记（true：各数据源之间冲突字典保留优先级高的，false：合并各数据源数据）
     * @param dictPackInitJobs 初始化流程（传入顺序表示优先级）
     * @author qishenghe
     * @date 2021/6/7 11:15
     * @change 2021/6/7 11:15 by qishenghe for init
     * @since 1.0.0
     */
    private void refreshPack(boolean dictPackMutex, DictPackInitJob... dictPackInitJobs) {

        DictPack dictPack;
        if (dictPackMutex) {
            // 各数据源字典间互斥
            List<DictPack> childrenDictPacks = new LinkedList<>();
            for (DictPackInitJob singleJob : dictPackInitJobs) {
                // 生成容器
                DictPack singleDictPack = DictPack.createDictPackByInitData(singleJob.init());
                childrenDictPacks.add(singleDictPack);
            }
            // 合并多源容器
            dictPack = DictPack.merge(childrenDictPacks.toArray(new DictPack[0]));
        } else {
            // 合并各数据源字典数据
            List<DictEntity> initData = new LinkedList<>();
            for (DictPackInitJob singleJob : dictPackInitJobs) {
                initData.addAll(singleJob.init());
            }
            // 生成容器
            dictPack = DictPack.createDictPackByInitData(initData);
        }
        // 引用切换
        this.dictPack = dictPack;
    }

    /**
     * 刷新缓存容器（无参重载）
     *
     * @author qishenghe
     * @date 2021/6/7 11:24
     * @change 2021/6/7 11:24 by qishenghe for init
     * @since 1.0.0
     */
    public void refreshPack() {
        if (this.dictPackInitJobs != null && this.dictPackInitJobs.size() != 0) {
            refreshPack(this.dictPackMutex, this.dictPackInitJobs.toArray(new DictPackInitJob[0]));
        }
    }

    /**
     * 启动周期自刷新调度器
     */
    public void startDictPackAutoFresh() throws SchedulerException {
        if (this.dictPackAutoFreshScheduler == null) {
            dictPackAutoFreshScheduler = new StdSchedulerFactory().getScheduler();
        }
        // 调度器处于停止状态，可以正常配置并启动调度器
        // 清空当前任务
        dictPackAutoFreshScheduler.clear();
        // 配置Job和Trigger
        // 传入当前session
        String jobName = "DictPackAutoFresh";
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("session", this);

        JobDetail jobDetail = JobBuilder.newJob(DictPackAutoFreshJob.class).setJobData(jobDataMap)
                        .withIdentity(jobName).build();
        Trigger jobTrigger = TriggerBuilder.newTrigger().withIdentity(jobName)
                        .withSchedule(CronScheduleBuilder.cronSchedule(this.autoRefreshCron)).forJob(jobDetail).build();

        // 载入job
        dictPackAutoFreshScheduler.scheduleJob(jobDetail, jobTrigger);
        // 启动
        dictPackAutoFreshScheduler.start();
        /*
         * if (!dictPackAutoFreshScheduler.isShutdown()) { // 调度器未停止 throw new
         * SchedulerException("调度器处于运行状态，无法重复启动"); } else { }
         */
    }

    /**
     * 关闭周期自刷新调度器
     * 
     * @param now 强制关闭标识（true：立即关闭，false：发送关闭信号）
     */
    public void shutdownDictPackAutoFresh(boolean now) throws SchedulerException {
        if (this.dictPackAutoFreshScheduler != null) {
            dictPackAutoFreshScheduler.shutdown(now);
        }
    }

    /**
     * 重启周期自刷新调度器
     */
    public void restartDictPackAutoFresh(boolean now) throws SchedulerException {
        shutdownDictPackAutoFresh(now);
        while (true) {
            if (this.dictPackAutoFreshScheduler == null || this.dictPackAutoFreshScheduler.isShutdown()) {
                startDictPackAutoFresh();
                break;
            }
        }
    }

    /**
     * 字典控制会话（Builder）
     *
     * @author qishenghe
     * @date 2021/6/5 18:07
     * @change 2021/6/5 18:07 by qishenghe for init
     */
    public static class Builder {

        /**
         * 多源字典加载互斥标记
         */
        private Boolean dictPackMutex;

        /**
         * 字典缓存容器初始化Job
         */
        private List<DictPackInitJob> dictPackInitJobs;

        /**
         * 自刷新周期Cron表达式
         */
        private String autoRefreshCron;

        /**
         * 字典控制工具实例配置
         */
        private Map<String, String> dictCtrlUtilConfig = new HashMap<>(1);

        /**
         * 字典数据转换工具实例配置
         */
        private Map<String, String> dictTransUtilConfig = new HashMap<>(0);

        /**
         * 【set】设置多源字典加载互斥标记
         *
         * @param dictPackMutex 多源字典加载互斥标记
         * @return builder
         */
        public synchronized Builder setDictPackMutex(Boolean dictPackMutex) {
            this.dictPackMutex = dictPackMutex;
            return this;
        }

        /**
         * 【set】设置初始化过程
         *
         * @param dictPackInitJobs 初始化过程
         * @return builder
         */
        public synchronized Builder setDictPackInitJob(DictPackInitJob... dictPackInitJobs) {
            this.dictPackInitJobs = Arrays.asList(dictPackInitJobs);
            return this;
        }

        /**
         * 【set】设置自刷新周期表达式
         *
         * @param autoRefreshCron 自刷新周期表达式
         * @return builder
         */
        public synchronized Builder setAutoRefreshCron(String autoRefreshCron) {
            this.autoRefreshCron = autoRefreshCron;
            return this;
        }

        /**
         * 【set】设置字典控制工具配置
         *
         * @param key 配置项
         * @param value 配置内容
         * @return builder
         */
        public synchronized Builder setDictCtrlUtilConfig(String key, String value) {
            this.dictCtrlUtilConfig.put(key, value);
            return this;
        }

        /**
         * 【set 重载】设置字典控制工具配置
         *
         * @param configMap 配置
         * @return builder
         */
        public synchronized Builder setDictCtrlUtilConfig(Map<String, String> configMap) {
            this.dictCtrlUtilConfig.putAll(configMap);
            return this;
        }

        /**
         * 【create】根据配置生成_字典控制工具对象
         *
         * @param muninSession muninSession
         * @param dictCtrlUtilConfig 字典控制工具配置
         * @return 字典控制工具实例
         */
        private DictCtrlUtil createDictCtrlUtil(MuninSession muninSession, Map<String, String> dictCtrlUtilConfig) {
            // 【默认】只读属性
            boolean readOnly = DictCtrlUtil.DEFAULT_READONLY;
            for (String configKey : dictCtrlUtilConfig.keySet()) {
                // 【属性】只读
                if (DictCtrlUtil.CONFIG_READONLY.equals(configKey)) {
                    if (Boolean.TRUE.toString().equals(dictCtrlUtilConfig.get(configKey))) {
                        // readOnly
                        readOnly = true;
                    } else if (Boolean.FALSE.toString().equals(dictCtrlUtilConfig.get(configKey))) {
                        // !readOnly
                        readOnly = false;
                    }
                }
                // 【属性】。。。其他属性
            }

            // 生成对象
            DictCtrlUtil result = new DictCtrlUtil(muninSession);
            result.setReadOnly(readOnly);

            return result;
        }

        /**
         * 【set】设置字典数据转换工具
         *
         * @param key 配置项
         * @param value 配置内容
         * @return builder
         */
        public synchronized Builder setDictTransUtilConfig(String key, String value) {
            this.dictTransUtilConfig.put(key, value);
            return this;
        }

        /**
         * 【set 重载】设置字典数据转换工具
         *
         * @param configMap 配置
         * @return builder
         */
        public synchronized Builder setDictTransUtilConfig(Map<String, String> configMap) {
            this.dictTransUtilConfig.putAll(configMap);
            return this;
        }

        /**
         * 【create】根据配置生成_字典数据转换工具对象
         *
         * @param muninSession muninSession
         * @param dictTransUtilConfig 字典数据转换工具配置
         * @return 字典数据转换工具实例
         */
        private DictTransUtil createDictTransUtil(MuninSession muninSession,
                        Map<String, String> dictTransUtilConfig) {
            // 生成对象
            return new DictTransUtil(muninSession);
        }

        /**
         * 【get-default】获取默认_多源字典加载互斥标记
         *
         * @return default
         */
        private Boolean getDefaultDictPackMutex() {
            return true;
        }

        /**
         * 【get-default】获取默认_初始化过程
         *
         * @return default
         */
        private List<DictPackInitJob> getDefaultDictPackInitJob() {
            return null;
        }

        /**
         * 【get-default】获取默认_自刷新周期表达式
         *
         * @return default
         */
        private String getDefaultAutoRefreshCron() {
            return null;
        }

        /**
         * 生成Session
         *
         * @return dictSession
         */
        public synchronized MuninSession getOrCreate() {

            // 打印Banner
            MuninBannerPrinter.printBanner();

            MuninSession muninSession = new MuninSession();

            // 设置
            // 多源字典互斥标记
            muninSession.setDictPackMutex(
                            this.getDictPackMutex() == null ? getDefaultDictPackMutex() : this.getDictPackMutex());
            // 初始化流程
            muninSession.setDictPackInitJobs(this.getDictPackInitJobs() == null ? getDefaultDictPackInitJob()
                            : this.getDictPackInitJobs());
            // 自刷新周期
            muninSession.setAutoRefreshCron(this.getAutoRefreshCron() == null ? getDefaultAutoRefreshCron()
                            : this.getAutoRefreshCron());
            // 字典控制工具
            muninSession.setDictCtrlUtil(createDictCtrlUtil(muninSession, this.dictCtrlUtilConfig));
            // 字典数据转换工具
            muninSession.setDictTransUtil(createDictTransUtil(muninSession, this.dictTransUtilConfig));

            // 按流程预设加载字典数据进字典缓存容器
            muninSession.refreshPack();

            // 启动自刷新流程
            if (!StringUtils.isEmpty(this.autoRefreshCron)) {
                try {
                    muninSession.restartDictPackAutoFresh(true);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }

            return muninSession;
        }

        public synchronized Boolean getDictPackMutex() {
            return dictPackMutex;
        }

        public synchronized List<DictPackInitJob> getDictPackInitJobs() {
            return dictPackInitJobs;
        }

        public synchronized String getAutoRefreshCron() {
            return autoRefreshCron;
        }

        public synchronized Map<String, String> getDictCtrlUtilConfig() {
            return dictCtrlUtilConfig;
        }

        public synchronized Map<String, String> getDictTransUtilConfig() {
            return dictTransUtilConfig;
        }
    }

}
