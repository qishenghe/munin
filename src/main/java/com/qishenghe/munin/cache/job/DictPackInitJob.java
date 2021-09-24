package com.qishenghe.munin.cache.job;

import com.qishenghe.munin.cache.pack.DictEntity;

import java.util.List;

/**
 * 字典缓存容器初始化Job
 *
 * @author qishenghe
 * @date 2021/6/5 18:10
 * @change 2021/6/5 18:10 by qishenghe for init
 */
public interface DictPackInitJob {

    /**
     * 字典缓存容器初始化函数
     *
     * @return 字典缓存容器
     */
    List<DictEntity> init();

}
