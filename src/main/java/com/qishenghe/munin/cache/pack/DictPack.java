package com.qishenghe.munin.cache.pack;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 字典缓存容器
 *
 * @author qishenghe
 * @date 2021/6/5 18:09
 * @change 2021/6/5 18:09 by qishenghe for init
 */
@Data
public class DictPack implements Serializable {

    /**
     * 读写分离
     */
    ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    /**
     * 写
     */
    WriteLock writeLock = reentrantReadWriteLock.writeLock();

    /**
     * 读
     */
    ReadLock readLock = reentrantReadWriteLock.readLock();

    /**
     * 缓存容器
     */
    private Map<String, DictSinglePack> dictPack;

    /**
     * 合并缓存容器【生成合并后的副本并返回】
     *
     * @param packs 需要合并的各个子容器（注意传入顺序，表示合并优先级，前置容器优先级大）
     * @return 合并后的容器 （注：合并优先级，当Key值出现冲突，优先级大的覆盖优先级小的）
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/6/7 10:28
     * @change 2021/6/7 10:28 by qishenghe for init
     */
    public static DictPack merge(DictPack... packs) {
        // 降低加载因子，减少Hash冲突可能性，提升查询效率
        // 容器
        Map<String, DictSinglePack> tmpDictPack = new HashMap<>(100, 0.5f);

        // 优先级自低向高遍历，后入覆盖
        if (packs != null) {
            for (int i = packs.length - 1; i >= 0; i--) {
                DictPack singlePack = packs[i];
                tmpDictPack.putAll(singlePack.getDictPack());
            }
        } else {
            tmpDictPack = new HashMap<>(0);
        }

        // 生成容器副本
        DictPack result = new DictPack();
        result.setDictPack(tmpDictPack);

        return result;
    }

    /**
     * 根据接入的源数据生成字典容器
     *
     * @param initData 输入源数据
     * @return 字典容器
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/6/7 14:17
     * @change 2021/6/7 14:17 by qishenghe for init
     */
    public static DictPack createDictPackByInitData(List<DictEntity> initData) {
        DictPack result = new DictPack();
        if (initData == null || initData.size() == 0) {
            // 空
            result.setDictPack(new HashMap<>(0));
        } else {
            // 初始化数据预处理（sortNum空值处理，sortNum为空时赋值-1）
            setSortNumDefaultValueIfNull(initData);
            // 分组
            Map<String, List<DictEntity>> groupMap = new HashMap<>(initData.size());
            // 执行分组
            for (DictEntity single : initData) {
                // 字典编码
                String dictCode = single.getDictCode();
                if (!StringUtils.isEmpty(dictCode)) {
                    if (groupMap.containsKey(dictCode)) {
                        List<DictEntity> tmpList = groupMap.get(dictCode);
                        tmpList.add(single);
                        groupMap.put(dictCode, tmpList);
                    } else {
                        List<DictEntity> tmpList = new LinkedList<>();
                        tmpList.add(single);
                        groupMap.put(dictCode, tmpList);
                    }
                }
            }
            // 处理分组数据生成字典容器
            Map<String, DictSinglePack> resultDictPack = new HashMap<>(groupMap.size() << 1, 0.5f);
            for (String dictCode : groupMap.keySet()) {
                DictSinglePack singleDict = DictSinglePack.createSinglePackByTargetDictData(groupMap.get(dictCode));
                resultDictPack.put(dictCode, singleDict);
            }
            // 赋值
            result.setDictPack(resultDictPack);
        }
        return result;
    }

    /**
     * 【封装】初始化数据预处理（sortNum空值处理，sortNum为空时赋值-1）
     *
     * @param initData 初始化数据集
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/6/8 13:40
     * @change 2021/6/8 13:40 by qishenghe for init
     */
    private static void setSortNumDefaultValueIfNull(List<DictEntity> initData) {
        for (DictEntity single : initData) {
            if (single != null && single.getSortNum() == null) {
                single.setSortNum(-1);
            }
        }
    }

}
