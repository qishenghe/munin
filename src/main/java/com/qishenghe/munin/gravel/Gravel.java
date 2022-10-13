package com.qishenghe.munin.gravel;

import com.qishenghe.munin.cache.job.DictPackInitJob;

/**
 * 石子（用于快速搭建数据获取通道）
 *
 * @author qishenghe
 * @date 4/18/22 3:28 PM
 * @change 4/18/22 3:28 PM by shenghe.qi@relxtech.com for init
 */
public abstract class Gravel {

    /**
     * create init job
     *
     * @return dict pack init job
     * @author shenghe.qi
     * @date 10/9/22 11:46 AM
     * @change 10/9/22 11:46 AM by shenghe.qi for init
     */
    public abstract DictPackInitJob createInitJob ();

}
