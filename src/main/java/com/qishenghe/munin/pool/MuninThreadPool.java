package com.qishenghe.munin.pool;

import java.util.concurrent.ExecutorService;

/**
 * Munin线程池
 *
 * @author qishenghe
 * @date 12/29/21 9:35 AM
 * @change 12/29/21 9:35 AM by shenghe.qi@relxtech.com for init
 */
public abstract class MuninThreadPool {

    /**
     * 获取cpu密集型线程池
     * 
     * @return ExecutorService（cpu）
     * @since 1.0.0
     * @author qishenghe
     * @date 12/29/21 9:38 AM
     * @change 12/29/21 9:38 AM by shenghe.qi@relxtech.com for init
     */
    public abstract ExecutorService getThreadPoolCpu ();

    /**
     * 获取IO密集型线程池
     * 
     * @return ExecutorService（IO）
     * @since 1.0.0
     * @author qishenghe
     * @date 12/29/21 9:39 AM
     * @change 12/29/21 9:39 AM by shenghe.qi@relxtech.com for init
     */
    public abstract ExecutorService getThreadPoolIo ();

}
