package com.qishenghe.munin.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 默认Munin线程池
 *
 * @author qishenghe
 * @date 11/23/21 4:07 PM
 * @change 11/23/21 4:07 PM by shenghe.qi@relxtech.com for init
 */
public class DefaultMuninThreadPool extends MuninThreadPool {

    /**
     * 核心数
     */
    private static final int CORE_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 线程工厂【cpu密集型】
     */
    private static ThreadFactory nameFactoryIntenseCpu =
            new ThreadFactoryBuilder().setNameFormat("DefaultMuninThreadPool-IntenseCpu-%d").build();

    /**
     * 线程工厂【IO密集型】
     */
    private static ThreadFactory nameFactoryIntenseIo =
            new ThreadFactoryBuilder().setNameFormat("DefaultMuninThreadPool-IntenseIo-%d").build();

    /**
     * 线程池【cpu密集型】
     * <p>
     * 回收策略：无
     * 阻塞队列：定长（100）
     * 拒绝策略：CallerRunsPolicy
     */
    private static ExecutorService threadPoolIntenseCpu = new ThreadPoolExecutor(
            CORE_NUM + 1, CORE_NUM + 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100), nameFactoryIntenseCpu, new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 线程池【IO密集型】
     * <p>
     * 回收策略：无
     * 阻塞队列：定长（100）
     * 拒绝策略：CallerRunsPolicy
     */
    private static ExecutorService threadPoolIntenseIo = new ThreadPoolExecutor(
            (CORE_NUM << 1) + 1, (CORE_NUM << 1) + 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100), nameFactoryIntenseIo, new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 获取线程池实例【cpu密集型】
     *
     * @return 线程池
     */
    @Override
    public ExecutorService getThreadPoolCpu() {
        return threadPoolIntenseCpu;
    }

    /**
     * 获取线程池实例【IO密集型】
     *
     * @return 线程池
     */
    @Override
    public ExecutorService getThreadPoolIo() {
        return threadPoolIntenseIo;
    }
}
